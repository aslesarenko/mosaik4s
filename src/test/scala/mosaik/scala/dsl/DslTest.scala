package mosaik.scala.dsl

import org.ergoplatform.mosaik.model.ViewContent
import org.scalatest.propspec.AnyPropSpec
import org.scalatest.matchers.should.Matchers

class DslTest extends AnyPropSpec with Matchers {

  property("dsl") {
    
    val app = mosaikApp(appName = "Test", appVersion = 0) {

      showDialog("This is an error message") {
//        positiveButtonText = "Nothing"
      }
//
//      box {
//        // Drawback in Kotlin DSL: This box will be overwritten by next column
//      }
//
//      column {
//        box(Padding.DEFAULT) {
//          button("A button")
//        }
//
//        layout(HAlignment.START, 1) {
//          label("This is a label") {
//            style = LabelStyle.BODY1BOLD
//          }
//        }
//
//        box {
//
//          label("Another label. without weight and not bold.")
//        }
//      }

    }

  }
}
