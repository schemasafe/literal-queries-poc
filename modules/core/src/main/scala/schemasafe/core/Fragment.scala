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
import schemasafe.utils.Get
import singleton.ops._
import singleton.ops.impl.OpString
import com.github.ghik.silencer.silent

class Fragment[LQ <: XString](val query: LQ) extends AnyVal {
  def :+[LQ2 <: XString](postfix: LQ2)(implicit concat: OpString[query.type + postfix.type]): Fragment[concat.Out] =
    new Fragment[concat.Out](concat.value)

  def +:[LQ2 <: XString](prefix: LQ2)(implicit concat: OpString[prefix.type + query.type]): Fragment[concat.Out] =
    new Fragment[concat.Out](concat.value)

  @silent // param unused warning (we only need the type)
  def ++[LQ2 <: XString](rq: Fragment[LQ2])(implicit concat: OpString[query.type + LQ2]): Fragment[concat.Out] =
    new Fragment[concat.Out](concat.value)

  def apply[I, O]: FragmentWithTypes[LQ, I, O] =
    new FragmentWithTypes[LQ, I, O](query)
}

object Fragment {
  def apply[LQ <: XString](lq: LQ): Fragment[LQ] = new Fragment[LQ](lq)
}

class FragmentWithTypes[LQ <: XString, I, O](val lq: LQ) extends AnyVal {
  def materialize[R <: XResult[_], Success](
    implicit
    matOrError: Materializer.Aux[LQ, I, O, R],
    getter: Get.Aux[R, Success]
  ): Success = getter(matOrError.result)
}
