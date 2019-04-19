package project.animalquiz.yasaman.animalquiz;

import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

   public  void  onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);

       addPreferencesFromResource(R.xml.quiz_preferences);
   }
}
