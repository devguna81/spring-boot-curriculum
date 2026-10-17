package com.jqdom.curriculum.ch36.orchestration;

import java.util.List;

public record SummaryResult(List<MemberSummary> summaries, List<Long> missingIds,
                            List<String> warnings, List<CallOutcome> outcomes) {
}
