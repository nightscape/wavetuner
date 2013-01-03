package org.wavetuner
import _root_.android.app.{Activity, Dialog}
import _root_.android.view.View
import android.widget.ListView
import android.view.View.OnClickListener

case class TypedResource[T](id: Int)
case class TypedLayout(id: Int)

object TR {
  val programsView = TypedResource[ListView](R.id.program_list_view)
}
trait TypedViewHolder {
  def findViewById( id: Int ): View
  def findView[T](tr: TypedResource[T]) = findViewById(tr.id).asInstanceOf[T]
}
trait TypedView extends View with TypedViewHolder
trait TypedActivityHolder extends TypedViewHolder
trait TypedActivity extends Activity with TypedActivityHolder
trait TypedDialog extends Dialog with TypedViewHolder
object TypedResource {
  implicit def layout2int(l: TypedLayout) = l.id
  implicit def view2typed(v: View) = new TypedViewHolder { 
    def findViewById( id: Int ) = v.findViewById( id )
  }
  implicit def activity2typed(a: Activity) = new TypedViewHolder { 
    def findViewById( id: Int ) = a.findViewById( id )
  }
  implicit def dialog2typed(d: Dialog) = new TypedViewHolder { 
    def findViewById( id: Int ) = d.findViewById( id )
  }
}
trait ListenerConversions {
  implicit def function2onClickListener(f: View => Unit) = {
    new OnClickListener {
      override def onClick(view:View) {
        f(view)
      }
    }
  }
}
