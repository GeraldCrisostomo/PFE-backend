package scheduler.tasks

import play.api.inject.{SimpleModule, bind}

/**
 * Module Guice pour l'initialisation et le démarrage des tâches liées à TourneeManagerActor.
 * Ce module lie TourneeManagerActorTask en tant que singleton hâtif, ce qui signifie
 * qu'il sera lancé automatiquement lors du démarrage de l'application.
 */
class TasksModule extends SimpleModule(bind[TourneeManagerTask].toSelf.eagerly())
