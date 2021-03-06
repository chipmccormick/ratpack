/*
 * Copyright 2014 the original author or authors.
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

package ratpack.handling

import ratpack.test.internal.RatpackGroovyDslSpec

class ChainActionSpec extends RatpackGroovyDslSpec {

  def "can use ChainAction"() {
    given:
    def c = new ChainAction() {
      @Override
      protected void execute() throws Exception {
        get("foo") { it.render "foo" }
        get("bar") { it.render "bar" }
      }
    }

    when:
    handlers {
      handler chain(c)
    }

    then:
    getText("foo") == "foo"
    getText("bar") == "bar"
  }
}
