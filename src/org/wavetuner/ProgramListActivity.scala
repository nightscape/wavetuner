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
import android.scala.reactive.AndroidDomain._
import android.scala.reactive.ReactiveHandler

class ProgramListActivity extends FragmentActivity with ProgramListFragment.Callbacks with Observing {
  import TypedResource._
  import TR._
  def measurement = WaveTunerPrograms.measurement

  private var mTwoPane: Boolean = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    WaveTunerPrograms.initialize(new SoundPlayer(this))

    setContentView(R.layout.activity_program_list)
    if (findViewById(R.id.program_detail_container) != null) {
      mTwoPane = true
      getSupportFragmentManager.findFragmentById(R.id.program_list).asInstanceOf[ProgramListFragment]
        .setActivateOnItemClick(true)
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.activity_main, menu)
    val connectThinkgearDeviceMenuItem = menu.findItem(R.id.menu_connect_thinkgear)
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
    connectThinkgearDevice(connectThinkgearDeviceMenuItem)
    true
  }
  override def onOptionsItemSelected(item: android.view.MenuItem): Boolean = {
    item.getItemId() match {
      case R.id.menu_connect_thinkgear => connectThinkgearDevice(item); true
      case R.id.menu_use_mock_device =>
        WaveTunerPrograms.measurement = if (item.isChecked()) {
          new MockMeasurementSeries
        } else {
          new EegMeasurementSeries
        }
        item.setChecked(!item.isChecked())
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }
  def connectThinkgearDevice(item: MenuItem) {
    val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (btAdapter != null) {
      val tgDevice = new TGDevice(btAdapter, ReactiveHandler)
      observe(measurement.deviceStateChanges)(status => if(status == TGDevice.STATE_CONNECTED) tgDevice.start)
      tgDevice.connect(true)
    }
  }
  implicit def function2ViewOnItemClickListener[T <: android.widget.Adapter](f: (AdapterView[_], View, Int, Long) => Unit): AdapterView.OnItemClickListener = {

    new AdapterView.OnItemClickListener() {
      def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        f(parent, view, position, id)
      }
    }

  }
  override def onItemSelected(id: String) {
    if (mTwoPane) {
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
