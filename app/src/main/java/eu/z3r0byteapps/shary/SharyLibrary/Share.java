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

import java.util.Date;

public class Share {
    private Date expire;
    private ShareRestriction restrictions;
    private ShareType type;
    private String comment;
    private String secret;
    private int id;

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public ShareRestriction getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(ShareRestriction restrictions) {
        this.restrictions = restrictions;
    }

    public ShareType getType() {
        return type;
    }

    public void setType(ShareType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Share{" +
                "expire=" + expire +
                ", restrictions=" + restrictions +
                ", type=" + type +
                ", comment='" + comment + '\'' +
                ", secret='" + secret + '\'' +
                ", id=" + id +
                '}';
    }
}
