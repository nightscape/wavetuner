package org.wavetuner

import android.R
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import ProgramListFragment._
import scala.collection.JavaConversions._
import android.widget.AdapterView
import android.widget.AbsListView
import org.wavetuner.programs.WaveTunerPrograms
import java.util.ArrayList
import org.wavetuner.programs.Program

object ProgramListFragment {

  private val STATE_ACTIVATED_POSITION = "activated_position"

  trait Callbacks {

    def onItemSelected(id: String): Unit
  }

  private var sDummyCallbacks: Callbacks = new Callbacks() {

    override def onItemSelected(id: String) {
    }
  }
}

class ProgramListFragment extends ListFragment {

  private var mCallbacks: Callbacks = sDummyCallbacks

  private var mActivatedPosition: Int = AdapterView.INVALID_POSITION

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setListAdapter(new ArrayAdapter[Program](getActivity, android.R.layout.simple_list_item_activated_1,
      android.R.id.text1, WaveTunerPrograms.programs))
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState != null &&
      savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
      setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
    }
  }

  override def onAttach(activity: Activity) {
    super.onAttach(activity)
    if (!(activity.isInstanceOf[Callbacks])) {
      throw new IllegalStateException("Activity must implement fragment's callbacks.")
    }
    mCallbacks = activity.asInstanceOf[Callbacks]
  }

  override def onDetach() {
    super.onDetach()
    mCallbacks = sDummyCallbacks
  }

  override def onListItemClick(listView: ListView,
    view: View,
    position: Int,
    id: Long) {
    super.onListItemClick(listView, view, position, id)
    mCallbacks.onItemSelected(WaveTunerPrograms.programNames.get(position))
  }

  override def onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    if (mActivatedPosition != AdapterView.INVALID_POSITION) {
      outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition)
    }
  }

  def setActivateOnItemClick(activateOnItemClick: Boolean) {
    getListView.setChoiceMode(if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE)
  }

  def setActivatedPosition(position: Int) {
    if (position == AdapterView.INVALID_POSITION) {
      getListView.setItemChecked(mActivatedPosition, false)
    } else {
      getListView.setItemChecked(position, true)
    }
    mActivatedPosition = position
  }
}
