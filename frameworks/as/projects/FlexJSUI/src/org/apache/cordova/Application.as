////////////////////////////////////////////////////////////////////////////////
//
//  Licensed to the Apache Software Foundation (ASF) under one or more
//  contributor license agreements.  See the NOTICE file distributed with
//  this work for additional information regarding copyright ownership.
//  The ASF licenses this file to You under the Apache License, Version 2.0
//  (the "License"); you may not use this file except in compliance with
//  the License.  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////
package org.apache.cordova
{
	import org.apache.flex.events.Event;
	import org.apache.flex.core.Application;
	import org.apache.flex.core.IFlexInfo;

	/**
	 *  Dispatched at startup after the initial view has been
	 *  put on the display list.
	 *  
	 *  @langversion 3.0
	 *  @playerversion Flash 10.2
	 *  @playerversion AIR 2.6
	 *  @productversion FlexJS 0.0
	 */
	[Event(name="deviceready", type="org.apache.flex.events.Event")]

	/**
	 *  A customized Application that dispatches the Cordova deviceReady event
	 *  
	 *  @langversion 3.0
	 *  @playerversion Flash 10.2
	 *  @playerversion AIR 2.6
	 *  @productversion FlexJS 0.0
	 */
	public class Application extends org.apache.flex.core.Application implements IFlexInfo
	{
		public function Application()
		{
			addEventListener("applicationComplete", appCompleteHandler);
		}
		
		private function appCompleteHandler(event:Event):void
		{
			dispatchEvent(new Event("deviceready"));
		}
	}
}