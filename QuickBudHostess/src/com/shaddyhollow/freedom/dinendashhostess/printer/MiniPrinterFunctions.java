package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

public class MiniPrinterFunctions {
	enum BarcodeWidth {
		_125, _250, _375, _500, _625, _750, _875, _1_0
	};

	enum BarcodeType {
		code39, ITF, code93, code128
	};

	public static void AddRange(ArrayList<Byte> array, Byte[] newData) {
		for (int index = 0; index < newData.length; index++) {
			array.add(newData[index]);
		}
	}

	private static byte[] convertFromListByteArrayTobyteArray(
			List<Byte> ByteArray) {
		byte[] byteArray = new byte[ByteArray.size()];
		for (int index = 0; index < byteArray.length; index++) {
			if (null == ByteArray.get(index)) {
				byteArray[index] = 0;
			} else {
				byteArray[index] = ByteArray.get(index);
			}
		}

		return byteArray;
	}

	public static void sendCommand(Context context, String portName,
			String portSettings, ArrayList<Byte> byteList) throws Exception {
		StarIOPort port = null;
		try {
			/*
			 * using StarIOPort3.1.jar (support USB Port) Android OS Version:
			 * upper 2.2
			 */
			port = StarIOPort.getPort(portName, portSettings, 20000, context);
			/*
			 * using StarIOPort.jar Android OS Version: under 2.1 port =
			 * StarIOPort.getPort(portName, portSettings, 10000);
			 */
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			/*
			 * Mobile Printer Firmware Version 2.4 later, SM-S220i(Firmware
			 * Version 2.0 later)
			 * 
			 * Using Begin / End Checked Block method for preventing
			 * "data detective".
			 * 
			 * When sending large amounts of raster data, use Begin / End
			 * Checked Block method and adjust the value in the timeout in the
			 * "StarIOPort.getPort" in order to prevent "timeout" of the
			 * "endCheckedBlock method" while a printing.
			 * 
			 * If receipt print is success but timeout error occurs(Show message
			 * which is
			 * "There was no response of the printer within the timeout period."
			 * ), need to change value of timeout more longer in
			 * "StarIOPort.getPort" method. (e.g.) 10000 -> 30000When use
			 * "Begin / End Checked Block Sample Code", do comment out
			 * "query commands Sample code".
			 */

			/* Start of Begin / End Checked Block Sample code */
			StarPrinterStatus status = port.beginCheckedBlock();

			if (true == status.offline) {
				throw new StarIOPortException("A printer is offline");
			}

			byte[] commandToSendToPrinter = convertFromListByteArrayTobyteArray(byteList);
			port.writePort(commandToSendToPrinter, 0,
					commandToSendToPrinter.length);

			status = port.endCheckedBlock();

			if (true == status.coverOpen) {
				throw new StarIOPortException("Printer cover is open");
			} else if (true == status.receiptPaperEmpty) {
				throw new StarIOPortException("Receipt paper is empty");
			} else if (true == status.offline) {
				throw new StarIOPortException("Printer is offline");
			}
			/* End of Begin / End Checked Block Sample code */

			/*
			 * Mobile Printer Firmware Version 2.3 earlier
			 * 
			 * Using query commands for preventing "data detective".
			 * 
			 * When sending large amounts of raster data, send query commands
			 * after writePort data for confirming the end of printing and
			 * adjust the value in the timeout in the
			 * "checkPrinterSendToComplete" method in order to prevent "timeout"
			 * of the "sending query commands" while a printing.
			 * 
			 * If receipt print is success but timeout error occurs(Show message
			 * which is
			 * "There was no response of the printer within the timeout period."
			 * ), need to change value of timeout more longer in
			 * "checkPrinterSendToComplete" method. (e.g.) 10000 -> 30000When
			 * use "query commands Sample code", do comment out
			 * "Begin / End Checked Block Sample Code".
			 */

			/* Start of query commands Sample code */
			// byte[] commandToSendToPrinter =
			// convertFromListByteArrayTobyteArray(byteList);
			// port.writePort(commandToSendToPrinter, 0,
			// commandToSendToPrinter.length);
			//
			// checkPrinterSendToComplete(port);
			/* End of query commands Sample code */
		} catch (StarIOPortException e) {
			throw e;
//			 Builder dialog = new AlertDialog.Builder(context);
//			 dialog.setNegativeButton("Ok", null);
//			 AlertDialog alert = dialog.create();
//			 alert.setTitle("Failure");
//			 alert.setMessage(e.getMessage());
//			 alert.show();
		} finally {
			if (port != null) {
				try {
					StarIOPort.releasePort(port);
				} catch (StarIOPortException e) {
				}
			}
		}
	}
}
