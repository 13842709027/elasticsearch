/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.search.aggregations.metrics;

import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class TDigestPercentilesAggregator extends AbstractTDigestPercentilesAggregator {

    TDigestPercentilesAggregator(String name,
                                 ValuesSource valuesSource,
                                    SearchContext context,
                                    Aggregator parent,
                                    double[] percents,
                                    double compression,
                                    boolean keyed,
                                    DocValueFormat formatter,
                                    List<PipelineAggregator> pipelineAggregators,
                                    Map<String, Object> metaData) throws IOException {
        super(name, valuesSource, context, parent, percents, compression, keyed, formatter, pipelineAggregators, metaData);
    }

    @Override
    public InternalAggregation buildAggregation(long owningBucketOrdinal) {
        TDigestState state = getState(owningBucketOrdinal);
        if (state == null) {
            return buildEmptyAggregation();
        } else {
            return new InternalTDigestPercentiles(name, keys, state, keyed, formatter, pipelineAggregators(), metaData());
        }
    }

    @Override
    public double metric(String name, long bucketOrd) {
        TDigestState state = getState(bucketOrd);
        if (state == null) {
            return Double.NaN;
        } else {
            return state.quantile(Double.parseDouble(name) / 100);
        }
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new InternalTDigestPercentiles(name, keys, new TDigestState(compression), keyed,
            formatter, pipelineAggregators(), metaData());
    }
}
