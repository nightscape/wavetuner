package org.wavetuner

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NavUtils
import android.view.MenuItem
import scala.collection.JavaConversions._
import android.view.Menu
import org.wavetuner.eeg.MeasurementSeries
import com.neurosky.thinkgear.TGDevice
import android.bluetooth.BluetoothAdapter
import android.widget.AdapterView
import android.view.View
import android.media.AudioManager
import org.wavetuner.eeg.EegMeasurementSeries
import org.wavetuner.programs.WaveTunerPrograms
import org.wavetuner.feedback.audio.SoundPlayer
import org.wavetuner.eeg.MockMeasurementSeries
import org.wavetuner.eeg.EegMeasurementSeries
import org.wavetuner.react.AndroidDomain._
import org.wavetuner.react.ReactiveHandler

class ProgramListActivity extends FragmentActivity with ProgramListFragment.Callbacks with Observing {
  import TypedResource._
  import TR._
  def measurement = WaveTunerPrograms.measurement
  var currentDeviceStateObserver: Option[Observer] = None

  lazy val isTwoPane: Boolean = findViewById(R.id.program_detail_container) != null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    WaveTunerPrograms.initialize(new SoundPlayer(this))

    setContentView(R.layout.program_list_fragment)
    if (isTwoPane) {
      getSupportFragmentManager.findFragmentById(R.id.program_list_fragment).asInstanceOf[ProgramListFragment]
        .setActivateOnItemClick(true)
    }
  }
//  override def onStart = super.onStart; startMeasuring

  override def onPrepareOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.activity_main, menu)
    currentDeviceStateObserver.foreach(_.dispose)
    currentDeviceStateObserver = Some(installConnectionItemObserver(menu.findItem(R.id.menu_connect_thinkgear)))
    true
  }
  def installConnectionItemObserver(connectThinkgearDeviceMenuItem: MenuItem): Observer = {
    observe(measurement.deviceStateChanges) { newState: Int =>
      import TGDevice._
      val newIcon = getResources().getDrawable(newState match {
        case STATE_IDLE => R.drawable.conn_fit1
        case STATE_CONNECTING => R.drawable.conn_fit2
        case STATE_DISCONNECTED | STATE_NOT_FOUND | STATE_NOT_PAIRED => R.drawable.conn_bad
        case STATE_CONNECTED => R.drawable.conn_best
        case _ => R.drawable.conn_fit3;
      })
      connectThinkgearDeviceMenuItem.setIcon(newIcon)
    }
  }
  override def onOptionsItemSelected(item: android.view.MenuItem): Boolean = {
    item.getItemId() match {
      case R.id.menu_connect_thinkgear =>
        startMeasuring; true
      case R.id.menu_use_mock_device =>
        val shouldUseMock = !item.isChecked()
        item.setChecked(shouldUseMock)
        WaveTunerPrograms.useMock(shouldUseMock)
        startMeasuring
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }
  def startMeasuring {
    invalidateOptionsMenu()
    WaveTunerPrograms.measurement.start
  }
  implicit def function2ViewOnItemClickListener[T <: android.widget.Adapter](f: (AdapterView[_], View, Int, Long) => Unit): AdapterView.OnItemClickListener = {

    new AdapterView.OnItemClickListener() {
      def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        f(parent, view, position, id)
      }
    }

  }
  override def onItemSelected(id: String) {
    if (isTwoPane) {
      val arguments = new Bundle()
      arguments.putString(ProgramDetailFragment.ARG_ITEM_ID, id)
      val fragment = new ProgramDetailFragment()
      fragment.setArguments(arguments)
      getSupportFragmentManager.beginTransaction().replace(R.id.program_detail_container, fragment)
        .commit()
    } else {
      val detailIntent = new Intent(this, classOf[ProgramDetailActivity])
      detailIntent.putExtra(ProgramDetailFragment.ARG_ITEM_ID, id)
      startActivity(detailIntent)
    }
  }
}
