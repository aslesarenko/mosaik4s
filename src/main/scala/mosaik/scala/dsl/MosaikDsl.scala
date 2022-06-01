package mosaik.scala.dsl

import org.ergoplatform.mosaik.model.actions.{Action, DialogAction}
import org.ergoplatform.mosaik.model.ui.layout.Box
import org.ergoplatform.mosaik.model.{MosaikContext, MosaikManifest, ViewContent, MosaikApp}

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

