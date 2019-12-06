/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package io.siddhi.langserver.completion.providers.common;

import io.siddhi.langserver.completion.providers.CompletionProvider;
import io.siddhi.langserver.utils.SnippetBlockUtil;
import io.siddhi.query.compiler.SiddhiQLParser;
import org.eclipse.lsp4j.CompletionItem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides completions for outputEventTypeContext.
 * {@link io.siddhi.query.compiler.SiddhiQLParser.Output_event_typeContext}.
 */
public class OutputEventTypeContextProvider extends CompletionProvider {

    public OutputEventTypeContextProvider() {
        this.providerName = SiddhiQLParser.Output_event_typeContext.class.getName();
    }

    @Override
    public List<CompletionItem> getCompletions() {
        List<Map<String, Object>> suggestions = Arrays.asList(
                SnippetBlockUtil.SNIPPETS.get("KEYWORD_ALL_EVENTS"),
                SnippetBlockUtil.SNIPPETS.get("KEYWORD_CURRENT_EVENTS"),
                SnippetBlockUtil.SNIPPETS.get("KEYWORD_EXPIRED_EVENTS"),
                SnippetBlockUtil.SNIPPETS.get("KEYWORD_CURRENT"),
                SnippetBlockUtil.SNIPPETS.get("KEYWORD_EVENTS"));
        return generateCompletionList(suggestions);
    }
}