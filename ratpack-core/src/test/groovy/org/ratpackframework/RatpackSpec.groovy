/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ratpackframework

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.ratpackframework.app.Config
import org.ratpackframework.app.RatpackApp
import org.ratpackframework.app.RatpackAppFactory
import org.ratpackframework.app.internal.ConfigScript
import org.ratpackframework.util.CookieManager
import org.vertx.java.core.Vertx
import spock.lang.Specification

class RatpackSpec extends Specification {

  @Rule TemporaryFolder temporaryFolder

  RatpackApp app
  CookieManager cookieManager = new CookieManager()

  Config config

  File getRatpackFile() {
    file("ratpack.groovy")
  }

  File templateFile(String path) {
    file("templates/$path")
  }

  File publicFile(String path) {
    file("public/$path")
  }

  File file(String path) {
    def file = new File(temporaryFolder.root, path)
    assert file.parentFile.mkdirs() || file.parentFile.exists()
    file
  }

  def setup() {
    config = new ConfigScript(temporaryFolder.root)
  }

  def startApp() {
    app = new RatpackAppFactory().create(Vertx.newVertx(), config)
    config.baseDir(temporaryFolder.root)
    app.start()

  }

  def cleanup() {
    app?.stop()
  }

  HttpURLConnection urlGetConnection(String path = "") {
    urlConnection(path, "get")
  }

  HttpURLConnection urlPostConnection(String path = "") {
    urlConnection(path, "post")
  }

  HttpURLConnection urlConnection(String path = "", String method) {
    def connection = new URL("http://localhost:$app.port/$path").openConnection() as HttpURLConnection
    connection.allowUserInteraction = true
    connection.requestMethod = method.toUpperCase()
    cookieManager.setCookies(connection)
    try {
      connection.connect()
    } catch (IOException ignore) {

    }
    cookieManager.storeCookies(connection)
    connection
  }

  String urlGetText(String path = "") {
    new String(urlGetConnection(path).inputStream.bytes)
  }

  String urlPostText(String path = "") {
    new String(urlPostConnection(path).inputStream.bytes)
  }

  String errorGetText(String path = "") {
    new String(urlGetConnection(path).errorStream.bytes)
  }
}