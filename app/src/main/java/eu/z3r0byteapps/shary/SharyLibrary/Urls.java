/*
 * Copyright (c) 2018-2018 Bas van den Boom 'Z3r0byte'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.z3r0byteapps.shary.SharyLibrary;

public class Urls {
    public static final String common = "https://shary.z3r0byteapps.eu/api/";
    public static final String createUser = common + "add/user/";
    public static final String shares = common + "get/shares/";
    public static final String addShare = common + "add/share/";
    public static final String expire = common + "expire/share/";
    public static final String updateShare = common + "update/share/";
    public static final String get = common + "get/";
    public static final String getShare = get + "share/?s=";
    public static final String getCalendar = get + "calendar/?date=";
    public static final String getNewGrades = get + "newgrades/?s=";
}
