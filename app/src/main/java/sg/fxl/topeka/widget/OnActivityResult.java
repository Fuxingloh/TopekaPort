package sg.fxl.topeka.widget;

import android.content.Intent;

/**
 * Created by: Fuxing
 * Date: 27/1/2016
 * Time: 6:52 PM
 * Project: Topeka Port
 */
public interface OnActivityResult {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
