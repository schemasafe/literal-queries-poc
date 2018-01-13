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

import singleton.ops.{XLong, XString}


/**
  * @tparam LQ Literal String Query
  * @tparam I User case class representing Params or Input
  * @tparam O User case class representing Row or Output
  * @tparam S Max size of the Result set (if known)
  * @tparam RO Row data representation of output (usually ResultSet)
  * @tparam RI Row data representation of input (usually PreparedStatement => PreparedStatement)
  * @tparam E Error type in case encoder failed (usually Exception or String)
  */
trait Query[LQ <: XString, I, O, S <: Option[XLong], RO, RI, E] {
  /**
    * String representation of LQ
    */
  def rawQuery: LQ

  /**
    * Maximum size of result set, as inferred from the query.
    * If None means the query didn't have limit clause.
    */
  def resultSetMaxSize: S

  /**
    * Encodes the user data types representing the query params to format suitable to be sent on wire (bytes probably)
    */
  def inputEncoder: I => RI

  /**
    * Decodes the result set returned by executing the query to the user type representing the selected columns/fields.
    */
  def outputDecoder: RO => Either[E, O]
}