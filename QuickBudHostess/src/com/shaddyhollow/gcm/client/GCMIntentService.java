/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.shaddyhollow.gcm.client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends GCMBaseIntentServiceCompat {
  public GCMIntentService() {
    super("GCMIntentService");
  }

  @Override
  protected void onMessage(Intent message) {
    dumpEvent("onMessage", message);
  }

  @Override
  protected void onError(Intent message) {
    dumpEvent("onError", message);
  }

  @Override
  protected void onDeleted(Intent message) {
    dumpEvent("onDeleted", message);
  }

  private void dumpEvent(String event, Intent message) {
    Bundle extras=message.getExtras();

    for (String key : extras.keySet()) {
      Log.d(getClass().getSimpleName(),
            String.format("%s: %s=%s", event, key,
                          extras.getString(key)));
    }
  }
}
