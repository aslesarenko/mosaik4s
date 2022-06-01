package mosaik.scala.dsl

import org.ergoplatform.mosaik.model.ViewContent
import org.ergoplatform.mosaik.model.actions.DialogAction
import org.ergoplatform.mosaik.model.ui.layout.{HAlignment, Padding}
import org.ergoplatform.mosaik.model.ui.text.LabelStyle
import org.ergoplatform.mosaik.serialization.MosaikSerializer
import org.scalatest.propspec.AnyPropSpec
import org.scalatest.matchers.should.Matchers

class DslTest extends AnyPropSpec with Matchers {

  def positiveButtonText(text: String)(using a: DialogAction): Unit = a.setPositiveButtonText(text)

  property("dsl example") {
    
    val app = mosaikApp(appName = "Test", appVersion = 0) {

      showDialog("This is an error message") {
        positiveButtonText("Nothing")
      }

      box {
        // Drawback in Kotlin DSL: This box will be overwritten by next column
      }

      column() {
        box(Padding.DEFAULT) {
          button("A button") {}
        }

        layout(HAlignment.START, 1) {
          label("This is a label", Some(LabelStyle.BODY1BOLD)) {}
        }

        box {
          label("Another label. without weight and not bold.") {}
        }
      }

    }

    val json = new MosaikSerializer().toJson(app)
    println(json)
    json shouldNot be(null)
  }
}
