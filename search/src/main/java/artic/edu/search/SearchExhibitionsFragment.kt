package artic.edu.search

import kotlin.reflect.KClass

class SearchExhibitionsFragment : SearchBaseFragment<SearchExhibitionsViewModel>() {
    override val viewModelClass: KClass<SearchExhibitionsViewModel> = SearchExhibitionsViewModel::class

}