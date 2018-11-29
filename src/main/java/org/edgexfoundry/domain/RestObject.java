/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice: device-rest
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.domain;

import org.edgexfoundry.domain.RestAttribute;
import org.edgexfoundry.domain.meta.DeviceObject;

public class RestObject extends DeviceObject {

  private RestAttribute attributes;

  public RestObject(DeviceObject object) {
    this.setName(object.getName());
    this.setTag(object.getTag());
    this.setDescription(object.getDescription());
    this.setProperties(object.getProperties());
    this.setAttributes(new RestAttribute(object.getAttributes()));
  }

  @Override
  public RestAttribute getAttributes() {
    return attributes;
  }

  public void setAttributes(RestAttribute attributes) {
    this.attributes = attributes;
  }

  @Override
  public String toString() {
    return "RestObject [ " + super.toString() + ", attributes=" + attributes + "]";
  }

}
