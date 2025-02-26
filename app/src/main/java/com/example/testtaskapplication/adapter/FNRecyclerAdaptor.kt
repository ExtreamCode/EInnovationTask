package com.example.testtaskapplication.Utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView


class FNRecyclerAdaptor<T>() : RecyclerView.Adapter<FNRecyclerAdaptor.ViewHolder>(),
    Filterable {
    private var mSearchResults: ArrayList<T>? = null
    private var mFilterResultCallBack: FilterResultCallBack? = null
    private var mListViewFilter: FNListFilter? = null

    private var mItems: ArrayList<T>? = null
    private var mRecyclerItemCreator: SGRecyclerRowCreator<T>? = null
    private var mRowResId = 0

    constructor(
        mItems: ArrayList<T>?,
        mRecyclerItemCreator: SGRecyclerRowCreator<T>,
        mRowResId: Int
    ) : this() {
        this.mRowResId = mRowResId
        this.mRecyclerItemCreator = mRecyclerItemCreator
        setItems(mItems)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mRowResId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowObject = mItems!![position]
        mRecyclerItemCreator?.createRecyclerRow(holder.view, rowObject, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mItems!!.size
    }

    fun getItems(): ArrayList<T>? {
        return mItems
    }

    public fun setItems(listItems: ArrayList<T>?) {
        mItems = if (listItems != null) ArrayList(listItems) else ArrayList()
        mSearchResults =
            if (listItems != null) ArrayList(listItems) else ArrayList()
        notifyDataSetChanged()
    }

    var filterResultCallBack: FilterResultCallBack?
        get() = mFilterResultCallBack
        set(mFilterResultCallBack) {
            this.mFilterResultCallBack = mFilterResultCallBack
        }

    override fun getFilter(): Filter {
        if (mListViewFilter == null) {
            mListViewFilter = FNListFilter()
        }
        return mListViewFilter as FNListFilter
    }

    fun removeAt(position: Int) {
        if (position < 0 || position > mItems!!.size) {
            return
        }
        mItems!!.removeAt(position)
        mSearchResults!!.removeAt(position) // remove item from search list
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mItems!!.size)
    }

    fun removeElement(element: T) {
        val index = mItems!!.indexOf(element)
        removeAt(index)
    }

    fun insertAt(position: Int, element: T) {
        if (position < 0) {
            return
        }
        mItems!!.add(position, element)
        mSearchResults!!.add(position, element) // add newly item in search list
        notifyItemInserted(position)
    }

    fun insertAt(listItems: java.util.ArrayList<T>?) {
        val startPosition = mItems!!.size
        mItems!!.addAll(startPosition, listItems!!)
        mSearchResults!!.addAll(startPosition, listItems) // add newly item in search list
        val endPosition = if (mItems!!.size > 0) mItems!!.size - 1 else 0
        notifyItemRangeInserted(startPosition, endPosition)
    }

    fun notifyUpdate(position: Int) {
        notifyItemInserted(position)
    }

    fun notifyUpdate(position: Int, element: T) {
        mItems!![position] = element
        mSearchResults!![position] = element
        notifyItemChanged(position)
    }

    fun indexOf(element: T): Int {
        return mItems!!.indexOf(element)
    }

    fun updateItems(newList: ArrayList<T>) {
        mItems = ArrayList(newList)
        notifyDataSetChanged()
    }

    interface SGRecyclerRowCreator<T> {
        fun createRecyclerRow(convertView: View, rowObject: T, position: Int)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )

    interface FilterResultCallBack {
        fun onFilterComplete(constraint: CharSequence?, filteredItemsSize: Int)
    }

    private inner class FNListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            var searchResult: MutableList<T> = ArrayList()
            if (FNObjectUtil.isEmpty(constraint)) {
                searchResult = ArrayList(mSearchResults)
            } else {
                for (p in mSearchResults!!) {
                    if (p == constraint) {
                        searchResult.add(p)
                    }
                }
            }
            results.values = searchResult
            results.count = searchResult.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            getItems()?.clear()
            val filteredItems = if (results.count > 0) results.values as List<T> else ArrayList()
            val resultSize: Int = FNObjectUtil.size(filteredItems)
            if (resultSize > 0) {
                getItems()?.addAll(filteredItems)
            }
            notifyDataSetChanged()
            mFilterResultCallBack?.onFilterComplete(constraint, resultSize)
        }
    }
}
