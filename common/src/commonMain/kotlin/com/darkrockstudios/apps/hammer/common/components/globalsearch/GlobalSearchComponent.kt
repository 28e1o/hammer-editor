package com.darkrockstudios.apps.hammer.common.components.globalsearch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.darkrockstudios.apps.hammer.common.components.ProjectComponentBase
import com.darkrockstudios.apps.hammer.common.data.ProjectDef
import com.darkrockstudios.apps.hammer.common.data.projectInject
import com.darkrockstudios.apps.hammer.common.data.tagindex.TagIndexService

class GlobalSearchComponent(
	componentContext: ComponentContext,
	projectDef: ProjectDef,
	private val searchState: GlobalSearchState,
	private val onDismiss: () -> Unit,
	private val navigateToResult: (SearchResult) -> Unit,
	initialQuery: String? = null,
) : ProjectComponentBase(projectDef, componentContext), GlobalSearch {

	private val tagIndexService: TagIndexService by projectInject()

	override val state: Value<GlobalSearch.State>
		get() = searchState.state

	init {
		if (!initialQuery.isNullOrBlank()) {
			searchState.setQuery(initialQuery)
		}
		searchState.setAvailableTags(
			tagIndexService.getRankedTags(limit = TAG_FILTER_LIMIT).map { it.tag }
		)
	}

	override fun onQueryChanged(query: String) {
		searchState.setQuery(query)
	}

	override fun onFilterChanged(filter: GlobalSearchFilter) {
		searchState.setFilter(filter)
	}

	override fun onTagToggled(tag: String) {
		searchState.toggleTag(tag)
	}

	override fun onResultClicked(result: SearchResult) {
		navigateToResult(result)
	}

	override fun dismiss() {
		onDismiss()
	}

	companion object {
		// Enough chips to be useful without drowning the dialog; ranked by usage.
		private const val TAG_FILTER_LIMIT = 24
	}
}
