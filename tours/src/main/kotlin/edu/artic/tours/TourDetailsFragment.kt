package edu.artic.tours

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.fuzz.rx.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.jakewharton.rxbinding2.widget.text
import edu.artic.adapter.*
import edu.artic.analytics.ScreenCategoryName
import edu.artic.base.utils.fromHtml
import edu.artic.db.models.ArticTour
import edu.artic.db.models.AudioFileModel
import edu.artic.language.LanguageAdapter
import edu.artic.language.LanguageSelectorViewBackground
import edu.artic.localization.SpecifiesLanguage
import edu.artic.map.MapActivity
import edu.artic.viewmodel.BaseViewModelFragment
import edu.artic.viewmodel.Navigate
import kotlinx.android.synthetic.main.cell_tour_details_stop.view.*
import kotlinx.android.synthetic.main.fragment_tour_details.*
import kotlin.reflect.KClass

class TourDetailsFragment : BaseViewModelFragment<TourDetailsViewModel>() {

    override val viewModelClass: KClass<TourDetailsViewModel>
        get() = TourDetailsViewModel::class
    override val layoutResId: Int
        get() = R.layout.fragment_tour_details
    override val title: String
        get() = ""

    override val screenCategory: ScreenCategoryName
        get() = ScreenCategoryName.TourDetails

    override fun hasTransparentStatusBar() = true


    private val tour: ArticTour get() = arguments!!.getParcelable(ARG_TOUR)

    private val translationsAdapter: BaseRecyclerViewAdapter<SpecifiesLanguage, BaseViewHolder>
        get() = languageSelector.adapter.baseRecyclerViewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            adapter = TourDetailsStopAdapter()
            val decoration = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(view.context, R.drawable.tour_detail_tour_stop_divider)?.let {
                decoration.setDrawable(it)
            }
            addItemDecoration(decoration)
            isNestedScrollingEnabled = true
        }

        languageSelector.adapter = LanguageAdapter().toBaseAdapter()

    }

    override fun onRegisterViewModel(viewModel: TourDetailsViewModel) {
        viewModel.tour = tour
    }

    override fun setupBindings(viewModel: TourDetailsViewModel) {
        viewModel.imageUrl
                .subscribe {
                    Glide.with(this)
                            .load(it)
                            .into(appBarLayout.detailImage)
                    Glide.with(this)
                            .load(it)
                            .into(tourDetailIntroCell.image)
                }.disposedBy(disposeBag)

        viewModel.titleText
                .subscribe { appBarLayout.setTitleText(it) }.disposedBy(disposeBag)

        viewModel.introductionTitleText
                .bindToMain(tourDetailIntroCell.tourStopTitle.text())
                .disposedBy(disposeBag)

        viewModel.stopsText
                .bindToMain(tourStops.text())
                .disposedBy(disposeBag)
        viewModel.timeText
                .bindToMain(tourTime.text())
                .disposedBy(disposeBag)
        viewModel.startTourButtonText
                .bindToMain(startTourButtonText.text())
                .disposedBy(disposeBag)
        viewModel.description
                .map { it.fromHtml() }
                .bindToMain(description.text())
                .disposedBy(disposeBag)
        viewModel.intro
                .map { it.fromHtml() }
                .bindToMain(intro.text())
                .disposedBy(disposeBag)

        viewModel.location
                .bindToMain(tourDetailIntroCell.tourStopGallery.text())
                .disposedBy(disposeBag)

        startTourButton.clicks()
                .defaultThrottle()
                .subscribe { viewModel.onClickStartTour() }
                .disposedBy(disposeBag)

        val adapter = recyclerView.adapter as TourDetailsStopAdapter
        viewModel.stops
                .bindToMain(adapter.itemChanges())
                .disposedBy(disposeBag)

        viewModel.availableTranslations
                .bindToMain(translationsAdapter.itemChanges())
                .disposedBy(disposeBag)

        LanguageSelectorViewBackground(languageSelector)
                .listenToLayoutChanges()
                .disposedBy(disposeBag)

        languageSelector
                .itemSelections()
                .distinctUntilChanged()
                .filter { position-> position>=0 }
                .map { position ->
                    val translation = translationsAdapter.getItem(position)
                    translation as ArticTour.Translation
                }.bindTo(viewModel.chosenTranslation)
                .disposedBy(disposeBag)

    }

    override fun setupNavigationBindings(viewModel: TourDetailsViewModel) {
        viewModel.navigateTo
                .filterTo<Navigate<TourDetailsViewModel.NavigationEndpoint>,
                        Navigate.Forward<TourDetailsViewModel.NavigationEndpoint>>()
                .subscribe { forward ->
                    val endpoint = forward.endpoint
                    when (endpoint) {
                        is TourDetailsViewModel.NavigationEndpoint.Map -> {
                            startActivity(MapActivity.getLaunchIntent(endpoint.tour))
                        }
                    }

                }.disposedBy(disposeBag)
    }

    companion object {
        private val ARG_TOUR = "${TourDetailsFragment::class.java.simpleName}:TOUR"

        fun argsBundle(tour: ArticTour) = Bundle().apply {
            putParcelable(ARG_TOUR, tour)
        }
    }
}