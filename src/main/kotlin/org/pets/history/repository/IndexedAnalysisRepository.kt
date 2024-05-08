package org.pets.history.repository

import org.pets.history.domain.IndexedAnalysis
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

interface IndexedAnalysisRepository : ElasticsearchRepository<IndexedAnalysis, String> {}
