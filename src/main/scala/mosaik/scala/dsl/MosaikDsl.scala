package mosaik.scala.dsl

import org.ergoplatform.mosaik.model.actions.{Action, DialogAction}
import org.ergoplatform.mosaik.model.ui.{ViewGroup, ForegroundColor, ViewElement}
import org.ergoplatform.mosaik.model.ui.layout.{HAlignment, Column, Padding, Box}
import org.ergoplatform.mosaik.model.ui.text.{Button, LabelStyle, Label}
import org.ergoplatform.mosaik.model.{MosaikContext, MosaikManifest, ViewContent, MosaikApp}

import java.text.NumberFormat.Style
import java.util.UUID

extension[T] (x: T)
  inline def apply(block: T ?=> Unit): T =
    block(using x)
    x

extension [T](iter: java.lang.Iterable[T])
  def none(predicate: T => Boolean): Boolean = iter match {
    case c: java.util.Collection[_] if c.isEmpty => true
    case _ =>
      val it = iter.iterator
      while (it.hasNext) {
        if (predicate(it.next())) return false
      }
      true
  }

def mosaikApp(
    appName: String, appVersion: Int,
    appDescription: Option[String] = None,
    appIconUrl: Option[String] = None,
    targetMosaikVersion: Int = MosaikContext.LIBRARY_MOSAIK_VERSION,
    targetCanvasDimension: Option[MosaikManifest.CanvasDimension] = None,
    cacheLifetime: Int = 0,
    errorReportUrl: Option[String] = None
)(init: MosaikApp ?=> Unit): MosaikApp = {
  given appInfo: MosaikApp = new MosaikApp()

  appInfo.setManifest(new MosaikManifest(
    appName,
    appVersion,
    targetMosaikVersion,
    targetCanvasDimension.orNull,
    cacheLifetime
  ).apply { it ?=>
    it.appDescription = appDescription.orNull
    it.iconUrl = appIconUrl.orNull
    it.errorReportUrl = errorReportUrl.orNull
  })
  appInfo.setView(new Box())
  init
  appInfo
}

def showDialog(
    message: String,
    id: Option[String] = None
  )( init: DialogAction ?=> Unit = {})
  (using view: ViewContent): DialogAction = {
    addAction(buildDialogAction(message), id)(init)
  }

def addAction[A <: Action](
      action: A,
      id: Option[String] = None,
  )(init: A ?=> Unit, setDefaultId: Boolean = id.isEmpty)
  (using view: ViewContent): A = {
    initAction(action, id)(setDefaultId, init)

    // add the action to the view content, if there is no equal one
    val currentActions = view.getActions
    if (currentActions.none { _ == action }) {
      currentActions.add(action)
      view.setActions(currentActions)
    }

    action
  }

def initAction[A <: Action](
  action: A,
  id: Option[String] = None,
)(setDefaultId: Boolean = id == null, init: A ?=> Unit): A = {
  if (setDefaultId) {
    action.setId(UUID.randomUUID().toString)
  } else {
    id.foreach(action.setId)
  }

  // and call the init with ViewContent as receiver
  init(using action)

  action
}

private def buildDialogAction(message: String): DialogAction = {
  val dialogAction = new DialogAction()
  dialogAction.setMessage(message)
  dialogAction
}

extension[G <: ViewGroup] (g: G)
  def groupElement[V <: ViewElement](viewElement: V)(init: V ?=> Unit): V = {
    g.addChild(viewElement)
    init(using viewElement)
    viewElement
  }

def viewElement[V <: ViewElement](viewElement: V)(init: V ?=> Unit)(using view: ViewContent): V = {
  view.setView(viewElement)
  init(using viewElement)
  viewElement
}

def box(init: Box ?=> Unit)(using view: ViewContent): Box = {
  viewElement(new Box())(init)
}

def column(padding: Option[Padding] = None)(init: Column ?=> Unit)(using view: ViewContent): Column = {
  viewElement(new Column().apply(c ?=>
    padding.foreach(c.setPadding)
  ))(init)
}

def button[G <: ViewGroup]
    (text: String, style: Option[Button.ButtonStyle] = None)
    (init: Button ?=> Unit = {})
    (using g: G): Button = {
  g.groupElement(new Button().apply(b ?=> {
    b.setText(text)
    style.foreach(b.setStyle)
  }))(init)
}

def label[G <: ViewGroup]
    (text: String, style: Option[LabelStyle] = None,
        textAlignment: Option[HAlignment] = None,
        textColor: Option[ForegroundColor] = None)
    (init: Label ?=> Unit = {})
    (using g: G): Label = {
  g.groupElement(new Label().apply(l ?=> {
    l.setText(text)
    style.foreach(l.setStyle)
    textAlignment.foreach(l.setTextAlignment)
    textColor.foreach(l.setTextColor)
  }))(init)
}

def layout
    (HAlignment: HAlignment, weight: Int = 0)
    (init: ViewGroup ?=> Unit)
    (using c: Column): Unit = {
  init
}