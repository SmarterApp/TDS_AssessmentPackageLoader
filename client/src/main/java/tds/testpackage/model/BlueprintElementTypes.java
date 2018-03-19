/***************************************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 **************************************************************************************************/

package tds.testpackage.model;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Represents all TDS-recognized blueprint element types
 */
public interface BlueprintElementTypes {
    String STRAND = "strand";
    String CONTENT_LEVEL = "contentlevel";
    String TARGET = "target";
    String CLAIM = "claim";
    String SOCK = "sock";
    String AFFINITY_GROUP = "affinitygroup";
    String SEGMENT = "segment";
    String TEST = "test";
    String PACKAGE = "package";
    String UNKNOWN = "unknown";

    Set<String> CLAIM_AND_TARGET_TYPES = ImmutableSet.of(STRAND, CONTENT_LEVEL, TARGET, CLAIM);
    Set<String> CLAIM_TYPES = ImmutableSet.of(STRAND, CLAIM);
    Set<String> TARGET_TYPES = ImmutableSet.of(CONTENT_LEVEL, TARGET);
}
