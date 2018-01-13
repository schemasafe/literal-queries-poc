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
package schemasafe.utils

import scala.reflect.macros.blackbox.Context
import schemasafe.core.utils.XResult
import singleton.ops.XString


trait Get[R <: XResult[_]] {
  type Out
  def apply(result: R): Out
}

object Get {
  type Aux[R <: XResult[_], O] = Get[R] { type Out = O }

  implicit def instance[E, O]: Aux[Right[Nothing, O], O] = new Get[Right[Nothing, O]] {
    override type Out = O
    override def apply(result: scala.Right[Nothing, Out]): Out = result.value
  }

  implicit def failedInstance[E <: XString]: Aux[Left[E, Nothing], Nothing] = macro fail[E]

  @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.ToString"))
  def fail[E <: XString](c: Context)(implicit eType: c.WeakTypeTag[E]): c.Expr[Aux[Left[E, Nothing], Nothing]] =
    c.abort(c.enclosingPosition, eType.tpe.toString)
}
