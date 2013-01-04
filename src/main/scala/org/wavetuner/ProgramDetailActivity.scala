package org.wavetuner

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NavUtils
import android.view.MenuItem
//remove if not needed
import scala.collection.JavaConversions._

class ProgramDetailActivity extends FragmentActivity {

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_program_detail)
    getActionBar.setDisplayHomeAsUpEnabled(true)
    if (savedInstanceState == null) {
      val arguments = new Bundle()
      arguments.putString(ProgramDetailFragment.ARG_ITEM_ID, getIntent.getStringExtra(ProgramDetailFragment.ARG_ITEM_ID))
      val fragment = new ProgramDetailFragment()
      fragment.setArguments(arguments)
      getSupportFragmentManager.beginTransaction().add(R.id.program_detail_container, fragment)
        .commit()
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (item.getItemId == android.R.id.home) {
      NavUtils.navigateUpTo(this, new Intent(this, classOf[ProgramListActivity]))
      return true
    }
    super.onOptionsItemSelected(item)
  }
}
