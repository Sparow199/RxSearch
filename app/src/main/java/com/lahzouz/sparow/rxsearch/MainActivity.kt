package com.lahzouz.sparow.rxsearch

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.main_activity.*

import android.view.Menu
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import com.orhanobut.logger.AndroidLogAdapter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var searchEngine: SearchEngine
    private val departmentAdapter = RecyclerViewAdapter()
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())
        setContentView(R.layout.main_activity)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = departmentAdapter

        searchEngine = SearchEngine(resources.getStringArray(R.array.departments))
        departmentAdapter.departments = searchEngine.getAll()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        disposable = rxSearchView(searchView).
                debounce(300, TimeUnit.MILLISECONDS)
                .filter { t: String -> t.length > 1 && !(t.startsWith(' ')) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showProgress() }
                .delay(700, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map { searchEngine.search(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    hideProgress()
                    showResult(it)
                }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun rxSearchView(searchView: SearchView): Observable<String> {

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                Logger.i("ONQUERY: " + query)
                subject.onComplete()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Logger.i("ONCHANGE: " + newText)
                if (!newText.isEmpty()) {
                    subject.onNext(newText)
                } else {
                    departmentAdapter.departments = searchEngine.getAll()
                }
                return true
            }

        })

        return subject
    }

    fun showProgress() {
        progressBar.visibility = VISIBLE
    }

    fun hideProgress() {
        progressBar.visibility = GONE
    }

    fun showResult(result: List<String>) {
        if (result.isEmpty()) {
            Toast.makeText(this, R.string.nothing_found, Toast.LENGTH_SHORT).show()
        }
        updateUi(result)
    }


    fun updateUi(result: List<String>) {
        if (!result.isEmpty()) {
            // if no books found, show a message
            departmentAdapter.departments = result
        }
    }
}