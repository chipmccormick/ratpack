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

package ratpack.path.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ratpack.path.PathBinder;
import ratpack.path.PathBinding;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenPathBinder implements PathBinder {

  private final ImmutableList<String> tokenNames;
  private final Pattern regex;

  protected TokenPathBinder(ImmutableList<String> tokenNames, Pattern regex) {
    this.tokenNames = tokenNames;
    this.regex = regex;
  }

  public PathBinding bind(String path, PathBinding parentBinding) {
    if (parentBinding != null) {
      path = parentBinding.getPastBinding();
    }
    Matcher matcher = regex.matcher(path);
    if (matcher.matches()) {
      MatchResult matchResult = matcher.toMatchResult();
      String boundPath = matchResult.group(1);
      ImmutableMap.Builder<String, String> paramsBuilder = ImmutableMap.builder();
      int i = 2;
      for (String name : tokenNames) {
        String value = matchResult.group(i++);
        if (value != null) {
          paramsBuilder.put(name, decodeURIComponent(value));
        }
      }

      return new DefaultPathBinding(path, boundPath, paramsBuilder.build(), parentBinding);
    } else {
      return null;
    }
  }

  private String decodeURIComponent(String s) {
    String str;
    try {
      str = URLDecoder.decode(s.replaceAll("\\+", "%2B"), "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
      throw new IllegalStateException("UTF-8 decoder should always be available");
    }
    return str;
  }
}
