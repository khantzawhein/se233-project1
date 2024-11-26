package com.se233.photoeditor.controllers.tasks;

import javafx.concurrent.Task;

abstract class BaseTask<V> extends Task<V> {
   abstract protected V work() throws Exception;
}
