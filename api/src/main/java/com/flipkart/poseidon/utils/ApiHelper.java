/*
 * Copyright 2015 Flipkart Internet, pvt ltd.
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
 */

package com.flipkart.poseidon.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.flipkart.poseidon.handlers.http.utils.StringUtils;
import com.flipkart.poseidon.helper.AnnotationHelper;
import com.flipkart.poseidon.helper.CallableNameHelper;
import com.flipkart.poseidon.helpers.ObjectMapperHelper;
import com.flipkart.poseidon.model.VariableModel;
import com.flipkart.poseidon.model.annotations.Name;
import com.flipkart.poseidon.model.annotations.Version;
import com.flipkart.poseidon.pojos.ParamPOJO;

import java.util.Optional;

/**
 * Created by venkata.lakshmi on 04/02/16.
 *
 *  Helper class which contains all util functions
 */
public class ApiHelper {

    public static String getUrlWithHttpMethod(String url, String httpMethod) {
        if (httpMethod == null) {
            return url;
        }
        // remove the extra slashes passed in the url
        return String.format("%s/%s", httpMethod, url ).replaceAll("[/]{2,}", "/");
    }

    public static String getFormattedUrl(String url) {
        if (url.startsWith("/") && !url.endsWith("/")) {
            return url;
        }
        // add a slash in front of the url, if absent
        String partiallyFormattedUrl = String.format("/%s", url );

        if (!url.endsWith("/")) {
            return partiallyFormattedUrl;
        }

        partiallyFormattedUrl = partiallyFormattedUrl.replaceAll("[/]{2,}", "/");
        return partiallyFormattedUrl.substring(0, partiallyFormattedUrl.length() - 1);
    }

    public static JavaType constructJavaType(ParamPOJO param) {
        final JavaType javaType;
        if (param.getType() != null) {
            javaType = constructJavaType(param.getType());
        } else if (!StringUtils.isNullOrEmpty(param.getJavatype())) {
            javaType = constructJavaType(param.getJavatype());
        } else {
            javaType = null;
        }
        return javaType;
    }

    private static JavaType constructJavaType(String type) {
        Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Specify a known class " + type, e);
        }

        return ObjectMapperHelper.getMapper().getTypeFactory().constructType(clazz);
    }

    private static JavaType constructJavaType(VariableModel variableModel) {
        Class<?> clazz;
        try {
            clazz = Class.forName(variableModel.getType());
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Specify a known class " + variableModel.getType(), e);
        }

        JavaType[] javaTypes = new JavaType[variableModel.getTypes().length];
        for (int i = 0; i < variableModel.getTypes().length; i++) {
            javaTypes[i] = constructJavaType(variableModel.getTypes()[i]);
        }

        return ObjectMapperHelper.getMapper().getTypeFactory().constructParametrizedType(clazz, clazz, javaTypes);
    }
}
