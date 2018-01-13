/*
 * Copyright 2016 Tamer AbdulRadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemasafe.core

import schemasafe.core.utils.XResult
import singleton.ops.XString

trait Materializer[LQ <: XString, I, O] {
  type Result <: XResult[Query[LQ, I, O, _, _, _, _]]
  def result: Result
}

object Materializer {
  type Aux[LQ <: XString, I, O, R <: XResult[_]] = Materializer[LQ, I, O] {
    type Result = R
  }

  def instance[LQ <: XString, I, O, R <: XResult[Query[LQ, I, O, _, _, _, _]]](res: R): Aux[LQ, I, O, R] =
    new Materializer[LQ, I, O] {
      override type Result = R
      override def result: Result = res
    }
}