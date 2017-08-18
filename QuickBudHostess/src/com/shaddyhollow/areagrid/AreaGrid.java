package com.shaddyhollow.areagrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.quicktable.models.Table.Status;
import com.shaddyhollow.util.Point;
//import java.util.UUID;

public class AreaGrid extends View {
	final static String _TAG = "AreaGrid";
	private int mColumns;
	private int mRows;
	private UUID floorplanID;
	private TablesAdapter tablesAdapter = null;
	private SectionsAdapter sectionsAdapter = null;
	private boolean drawGrid =  true;
	private boolean showTableStatus = true;
	private boolean showTableGroups = true;
	private boolean showTableSections = true;
	private boolean showTouch = true;
	private boolean showLargeTextForSelection = true;
	private int defaultTableColor = Color.GRAY;
	private Map<UUID, RectF> tileRectMap = new HashMap<UUID, RectF>();
	private ArrayList<Table> selectedTables = new ArrayList<Table>();
	
    ColorFilter lightingColorFilter = new LightingColorFilter(Color.BLACK, Color.WHITE );
	
	private int mTileWidth;
	private int mTileHeight;

	private final int mTileBorder = 1;
	
	public enum TileType { EMPTY, TABLE };
	
	private Paint TileBorder;
	private Paint TileHover;
	private Paint TileSelection;
	private Paint sectionPaint;
	private Paint highlight;
	private Paint tableInset;
	private Paint textPaint;
	private Paint commentPaint;
	private Paint combinedTablePaint;
	
	private Bitmap bmp; 
	
	private Path tablePath;
	
	private AreaGridListener mListener;
	
	private Point tileFirstPressed;
	private Point tileLastTouched;
	
	private boolean allowTouch = true;
	
	private boolean allowMultiSelection = true;
	
	public AreaGrid(Context context) {
		super(context);
		initPaints();
	}

	public AreaGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaints();
	}

	public AreaGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaints();
	}

	public AreaGrid(Context context, int columns, int rows) {
		super(context);
		mColumns = columns;
		mRows = rows;

		initPaints();
	}
	
	private void initPaints() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setDrawingCacheEnabled(false);

        TileBorder = new Paint();
		TileBorder.setColor( Color.DKGRAY );
		TileBorder.setAlpha(0xB0);
		
		TileHover = new Paint();
		TileHover.setColor( Color.YELLOW );
		TileHover.setAlpha(128);
		
		TileSelection = new Paint();
		TileSelection.setColor( Color.RED );
		TileSelection.setAlpha(128);
		
		sectionPaint = new Paint();
		sectionPaint.setColor( Color.WHITE );
		sectionPaint.setAlpha(0x44);
		
		highlight = new Paint();
		highlight.setColor( Color.WHITE );
		highlight.setAlpha(0xFF);
		highlight.setStyle(Paint.Style.STROKE);
		highlight.setStrokeWidth(4);
		highlight.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		tableInset = new Paint();
		tableInset.setColor( Color.WHITE );
		tableInset.setStyle(Paint.Style.FILL);
		tableInset.setAlpha(0xFF);
		tableInset.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		textPaint = new Paint();
		textPaint.setColor( Color.BLACK );
		textPaint.setTextSize(15);
		textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		textPaint.setAlpha(0xFF);
		textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		commentPaint = new Paint();
		commentPaint.setColor( Color.YELLOW );
		commentPaint.setAlpha(0xFF);

		combinedTablePaint = new Paint();
		
		tablePath = new Path();
		
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.diagonal_stripes);

	}
	
	private BitmapShader getGroupShader(int color) {
		Bitmap mutableBitmap;
//		if(mutableBitmap!=null) {
//			mutableBitmap.recycle();
//			mutableBitmap=null;
//		}
		mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);
		combinedTablePaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SCREEN));
		canvas.drawBitmap(mutableBitmap, new Matrix(), combinedTablePaint);

		BitmapShader colorShader = new BitmapShader(mutableBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		return colorShader;
	}
	
	public void setFloorplanID(UUID floorplanID) {
		this.floorplanID = floorplanID;
	}
	
	public void setSize(int rows, int columns) {
		mRows = rows;
		mColumns = columns;
	}

	public void setGridVisible(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}
	
	public void setListener(AreaGridListener listener) {
		this.mListener = listener;
	}

	public AreaGridListener getListener() {
		return mListener;
	}
	
	public void setAllowTouch(boolean allowTouch) {
		this.allowTouch = allowTouch;
	}

	public boolean getAllowTouch() {
		return allowTouch;
	}

	public void setAllowMultiSelection(boolean allowMultiSelection) {
		this.allowMultiSelection = allowMultiSelection;
	}

	public boolean getAllowMultiSelection() {
		return allowMultiSelection;
	}

	public boolean isType(TileType type, Point p) {
		boolean found = false;
		if(type.equals(TileType.TABLE)) {
			for(Table table : tablesAdapter.getTablesByFloorplan(floorplanID)) {
				if(table.position.contains(p)) {
					found = true;
					break;
				}
			}
		}
		if(type.equals(TileType.EMPTY)) {
			found = !found;
		}
		
		return found;
	}
	
	public Table getTableInTile(Point p) {
		Collection<Table> tables = tablesAdapter.getTablesByFloorplan(floorplanID);
		for (Table table: tables) {
			if (table.position.contains( p ) ) {
				return table;
			}
		}
		return null;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxSize = 0;
		int width = 0;
		int height = 0;
		if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		if (maxSize == 0) {
			// default when measuring error occurs (MeasureSpec.UNSPECIFIED)
			maxSize = 479;
		}
		setMeasuredDimension(width, height);
		recalculateSizes();
	}

	private void recalculateSizes() {
		mTileWidth  = (int) java.lang.Math.floor( getMeasuredWidth()  / mColumns  ) - mTileBorder;
		mTileHeight = (int) java.lang.Math.floor( getMeasuredHeight() / mRows ) - mTileBorder;
	}
	
	public static int fade(int c) {
		float diff = .8f;
		int a = Color.alpha(c);

		return Color.argb((int)(a * diff), Color.red(c), Color.green(c), Color.blue(c));
	}

	private void drawGrid(Canvas c) {
		if(drawGrid) {
			int width  = mColumns*(mTileWidth+mTileBorder);
			int height =  mRows*(mTileHeight+mTileBorder);

			for (int col = 0; col < mColumns+1; col++ ) {
				c.drawLine(col*(mTileWidth+mTileBorder), 0, col*(mTileWidth+mTileBorder), height, TileBorder);
			}
			for (int row = 0; row < mRows+1; row++ ) {
				c.drawLine(0, row*(mTileHeight+mTileBorder), width, row*(mTileHeight+mTileBorder), TileBorder);
			}
		}		
	}
	
	private void drawTouch(Canvas c) {
		if(!isShowTouch()) {
			return;
		}
		if(tileFirstPressed!=null && tileLastTouched!=null) {
			TileHover.setColor(Color.YELLOW);
			int startCol = Math.min(tileFirstPressed.column, tileLastTouched.column);
			int endCol = Math.max(tileFirstPressed.column, tileLastTouched.column);
			int startRow = Math.min(tileFirstPressed.row, tileLastTouched.row);
			int endRow = Math.max(tileFirstPressed.row, tileLastTouched.row);
			if(allowMultiSelection) {
				for (int column=startCol; column<=endCol; column++) {
					for (int row=startRow; row<=endRow; row++) {
						Rect r = getTileRect(column, row);
						c.drawRect( r, TileHover );
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param delta positive makes the area larger
	 * @return
	 */
	private void updateTablePath(RectF tileRect, Table table, float delta) {
		tablePath.reset();
		if("Diamond".equals(table.table_type)) {
			delta+=6;
			Matrix matrix = new Matrix();
			matrix.mapRect(tileRect);
			tablePath.moveTo(tileRect.left - delta, tileRect.centerY());
			tablePath.lineTo(tileRect.centerX(), tileRect.top - delta);
			tablePath.lineTo(tileRect.right + delta, tileRect.centerY());
			tablePath.lineTo(tileRect.centerX(), tileRect.bottom + delta);
		} else if("Circle".equals(table.table_type)) {
			tablePath.addCircle(tileRect.centerX(), tileRect.centerY(), (tileRect.width()/2)+delta, Direction.CW);
		} else { // default rectangular table
			tablePath.addRect(tileRect.left - delta, tileRect.top - delta, tileRect.right + delta,  tileRect.bottom + delta, Direction.CW);
		} 
		tablePath.close();
	}
	
	private void drawTableSelectionBorder(Canvas c, Collection<Table> allTables) {
		Table selectedTable = tablesAdapter.getCurrentSelection();
		ArrayList<Table> highlightedTables = new ArrayList<Table>();
		boolean isHighlighted;
		
		for(Table table : allTables) {
			if(isShowTableStatus()) {
				// if showing table status, all tables in selected group should be highlighted
				isHighlighted = selectedTable!=null && selectedTable.group_id!=null && selectedTable.group_id.equals(table.group_id);
			} else {
				// if not showing status, don't group tables.
				isHighlighted = selectedTable!=null && selectedTable.id.equals(table.id);
			}
			if(isHighlighted) {
				highlightedTables.add(table);
				RectF tileRect = tileRectMap.get(table.id);
				if(tileRect!=null) {
					updateTablePath(tileRect, table, 1);
					tableInset.setShader(null);
					tableInset.setColor(Color.WHITE);
					c.drawPath(tablePath, tableInset);
				}
			}
		}
	}
	
	private void drawTableSection(Canvas c, Collection<Table> allTables) {
		if(!isShowTableSections()) {
			return;
		}
		
		for(Table table : allTables) {
			Section section = null;
			if(table.section_id!=null) {
				section = sectionsAdapter.getItemByID(table.section_id);
			}
			
			if(section!=null) {
				sectionPaint.setColor(Config.getSectionPalette()[section.colorID]);
			} else {
				sectionPaint.setColor(Color.TRANSPARENT);
			}
			RectF tileRect = tileRectMap.get(table.id);
			if(tileRect!=null) {
				updateTablePath(tileRect, table, -3);
				c.drawPath(tablePath, sectionPaint);
			}
		}

	}
	
//	private void drawTableTops(Canvas c, Collection<Table> allTables) {
//		for(Table table : allTables) {
//			// draw table top (with status color)
//			tableInset.setAlpha(0x66);
//
//			tableInset.setColor(0xFFCCCCCC); // Color.LT_GREY
//			RectF tileRect = tileRectMap.get(table.id);
//			if(tileRect!=null) {
//				updateTablePath(tileRect, table, -5);
//				tableInset.setColorFilter(null);
//				tableInset.setShader(null);
//				c.drawPath(tablePath, tableInset);
//			}
//		}
//	}

	private void drawTableTops(Canvas c, Collection<Table> allTables, Collection<UUID> groups) {
		for(Table table : allTables) {
			int tableStatusColor = table.getStatusColor();
			Section section = null;
			if(table.section_id!=null) {
				section = sectionsAdapter.getItemByID(table.section_id);
			}
			tableInset.setColorFilter(null);
			tableInset.setShader(null);
			// draw table top (with status color)
			if(isShowTableStatus() && (section!=null && section.open)) {
				// draw table status
				tableInset.setAlpha(0x66);
				tableInset.setColor(tableStatusColor);
			} else {
				tableInset.setColor(0xFFCCCCCC); // Color.LT_GREY
			}

			RectF tileRect = tileRectMap.get(table.id);
			updateTablePath(tileRect, table, -5);
			if(!groups.contains(table.group_id)) {
				if(isShowTableStatus() && table.section_id!=null && table.seated_visit!=null && table.getState().equals(Status.OPEN)) {
					tableInset.setColor(Config.getSectionPalette()[section.colorID]);
					tableInset.setColorFilter(lightingColorFilter);
					tableInset.setAlpha(0xAA);
				}
			}
			c.drawPath(tablePath, tableInset);
		}
	}
	private void drawTableGroups(Canvas c, Collection<Table> allTables, Collection<UUID> groups) {
		for(Table table : allTables) {
			if(!groups.contains(table.group_id)) {
				continue;
			}

			Section section = null;
			if(table.section_id!=null) {
				section = sectionsAdapter.getItemByID(table.section_id);
			}

			RectF tileRect = tileRectMap.get(table.id);
			if(tileRect!=null) {
				updateTablePath(tileRect, table, -5);

				tableInset.setColor(sectionPaint.getColor() & 0xFFF0F0F0);
				if(isShowTableStatus() && section!=null && groups.contains(table.group_id)) {
					BitmapShader shader = getGroupShader(Config.getSectionPalette()[section.colorID]);
					if(shader==null) {
						System.out.println("Shader unexpectedly null");
					}
					tableInset.setShader(shader);
				} else {
					tableInset.setShader(null);
				}
				c.drawPath(tablePath, tableInset);
			}
		}

	}

	private void drawTableText(Canvas c, Collection<Table> allTables) {
		Table selectedTable = tablesAdapter.getCurrentSelection();
		for(Table table : allTables) {
			if(selectedTable!=null && isShowLargeTextForSelection()) {
				if(table.id.equals(selectedTable.id)) {
					textPaint.setTextSize(20);
				} else {
					textPaint.setTextSize(10);
				}
			} else {
				textPaint.setTextSize(15);
			}
			
			RectF tileRect = tileRectMap.get(table.id);
			String text = table.getDisplayText(isShowTableStatus());
			String[] lines = text.split("\n");
			float x = tileRect.centerX();
			float y = (tileRect.bottom - tileRect.top)/2 + tileRect.top - (lines.length * textPaint.descent());
			
	        for (String line : lines ) {
				float xOffset = textPaint.measureText(line)/2;

				textPaint.setColor(Color.WHITE);
				c.drawText(line, x-xOffset, y, textPaint);
				textPaint.setColor(Color.BLACK);
				c.drawText(line, x-xOffset+1, y+1, textPaint);
	            
				y += -textPaint.ascent() + textPaint.descent();
	        }
		}
	}
	
	private void drawTableComments(Canvas c, Collection<Table> allTables) {
		for(Table table : allTables) {
			RectF tileRect = tileRectMap.get(table.id);
			Section section = null;
			if(table.section_id!=null) {
				section = sectionsAdapter.getItemByID(table.section_id);
				sectionPaint.setColor(Config.getSectionPalette()[section.colorID]);
			} else {
				sectionPaint.setColor(Color.LTGRAY);
			}
			if(table.seated_visit!=null && table.seated_visit.comment!=null && table.seated_visit.comment.length()>0) {
				c.drawCircle(tileRect.right, tileRect.top, 12, sectionPaint);
				c.drawCircle(tileRect.right, tileRect.top, 10, commentPaint);
				c.drawText("C", tileRect.right - textPaint.measureText("C")/2, tileRect.top+textPaint.descent()+1, textPaint);
			}
		}
	}
	
	private void udpateTileRectMap(Collection<Table> allTables) {
		for(Table table : allTables) {
			RectF tileRect = convertTileRect(table.position);
			tileRectMap.put(table.id, tileRect);
		}
	}
	
	@Override
	public void onDraw(Canvas c) {
		drawGrid(c);
		drawTouch(c);


		udpateTileRectMap(tablesAdapter.getAll());
		Collection<Table> allTables = tablesAdapter.getTablesByFloorplan(floorplanID);
		Collection<UUID> groups = tablesAdapter.getGroupedTables(allTables);

		drawTableSelectionBorder(c, allTables);
		drawTableSection(c, allTables);
		drawTableTops(c, allTables, groups);
		drawTableGroups(c, allTables, groups);
		drawTableText(c, allTables);
		drawTableComments(c, allTables);
		
		selectedTables.clear();
		Table currentSelection = tablesAdapter.getCurrentSelection();
		if(currentSelection!=null && currentSelection.floorplan_id.equals(floorplanID)) {
			if(selectedTables.size()>0) {
				drawTableText(c, selectedTables);
			}
		}
	}

	/*
	 * convert from tile coordinates to x,y drawable coordinates
	 */
	private RectF convertTileRect(List<Point> points) {
		float left = 1000;
		float right = 0;
		float top = 1000;
		float bottom = 0;
		
		for(Point p : points) {
			left = Math.min(left, p.column);
			right = Math.max(right, p.column);
			top = Math.min(top, p.row);
			bottom = Math.max(bottom, p.row);
		}

		return new RectF( left * (mTileWidth+mTileBorder),
				top * (mTileHeight+mTileBorder),
				(right+1) * (mTileWidth+mTileBorder),
				(bottom+1) * (mTileHeight+mTileBorder));
	}
	
	private Rect getTileRect(int column, int row) {
		return new Rect( column * (mTileWidth+mTileBorder),
				row * (mTileHeight+mTileBorder),
				(column+1) * (mTileWidth+mTileBorder),
				(row+1) * (mTileHeight+mTileBorder));
	}

	private Point getTileWithPoint(int x, int y) {
		for (int column=0; column<mColumns; column++) {
			for (int row=0; row<mRows; row++) {
				if (getTileRect(column, row).contains(x, y)) {
					return new Point( row, column );
				}
			}
		}
		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		boolean handled = true;

		if (!allowTouch) {
			return true;
		}
		int x = (int)e.getX();
		int y = (int)e.getY();
		Point tile = getTileWithPoint( x, y );
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN :
				if ( tile != null ) {
					tileFirstPressed = tile;
					tileLastTouched = tile;
					postInvalidate();
				}
				break;
			case MotionEvent.ACTION_UP :
				if ( tile != null && mListener != null ) {
					if(tileFirstPressed!=null && tileLastTouched!=null) {
						if(tileFirstPressed.equals(tileLastTouched)) {
							mListener.onSingleTileHit( tileLastTouched );
						} else {
							if(mListener.allowMultiSelectionBetween(tileFirstPressed, tileLastTouched)) {
								mListener.onMultiTileHit(tileFirstPressed, tileLastTouched);
							}
						}
					}
				}
				
				tileFirstPressed = null;
				tileLastTouched = null;
				postInvalidate();
				break;
			case MotionEvent.ACTION_CANCEL :
				tileFirstPressed = null;
				tileLastTouched = null;
				postInvalidate();
				break;
			case MotionEvent.ACTION_OUTSIDE :
				tileFirstPressed = null;
				tileLastTouched = null;
				postInvalidate();
			case MotionEvent.ACTION_MOVE :
				if ( tile != null && tileLastTouched != null && !tile.equals(tileLastTouched) ) {
					tileLastTouched = tile;
					postInvalidate();
				}
				break;
				
			default: handled = false;
		}
		return handled;
	}
	
    private final DataSetObserver tableObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            postInvalidate();
        }

        @Override
        public void onInvalidated() {
            postInvalidate();
        }
    };

    public void setTablesAdapter(TablesAdapter adapter) {
        if (this.tablesAdapter != null) {
            this.tablesAdapter.unregisterDataSetObserver(tableObserver);
        }
        this.tablesAdapter = adapter;
        if (this.tablesAdapter != null) {
            this.tablesAdapter.registerDataSetObserver(tableObserver);
        }
        postInvalidate();
    }

    private final DataSetObserver sectionObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            postInvalidate();
        }

        @Override
        public void onInvalidated() {
            postInvalidate();
        }
    };

    public void setSectionsAdapter(SectionsAdapter adapter) {
        if (this.sectionsAdapter != null) {
            this.sectionsAdapter.unregisterDataSetObserver(sectionObserver);
        }
        this.sectionsAdapter = adapter;
        if (this.sectionsAdapter != null) {
            this.sectionsAdapter.registerDataSetObserver(sectionObserver);
        }
        postInvalidate();
    }

	public boolean isShowTableStatus() {
		return showTableStatus;
	}

	public void setShowTableStatus(boolean showTableStatus) {
		this.showTableStatus = showTableStatus;
	}

	public int getDefaultTableColor() {
		return defaultTableColor;
	}

	public void setDefaultTableColor(int defaultTableColor) {
		this.defaultTableColor = defaultTableColor;
	}

	public boolean isShowTableGroups() {
		return showTableGroups;
	}

	public void setShowTableGroups(boolean showTableGroups) {
		this.showTableGroups = showTableGroups;
	}

	public boolean isShowTableSections() {
		return showTableSections;
	}

	public void setShowTableSections(boolean showTableSections) {
		this.showTableSections = showTableSections;
	}

	public boolean isShowTouch() {
		return showTouch;
	}

	public void setShowTouch(boolean showTouch) {
		this.showTouch = showTouch;
	}

	public boolean isShowLargeTextForSelection() {
		return showLargeTextForSelection;
	}

	public void setShowLargeTextForSelection(boolean showLargeTextForSelection) {
		this.showLargeTextForSelection = showLargeTextForSelection;
	}
}
