package br.com.igorbag.githubsearch.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private var repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var btnShareListener: (Repository) -> Unit = {}
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(repository: Repository)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_item, parent, false)
//        Log.d("RepositoryAdapter", "ViewHolder created with repositoryName: ${view.findViewById<TextView>(R.id.tv_titulo_repositorio).text}")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]
        holder.apply {
            repositoryName.text = repository.name
            cardView.setOnClickListener {
                onItemClickListener?.onItemClick(repository)
            }
        }


        holder.btnShare.setOnClickListener {
            btnShareListener(repository)
        }


    }

    fun updateData(newRepositories: List<Repository>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repositoryName: TextView = view.findViewById(R.id.tv_titulo_repositorio)
        val btnShare: View = view.findViewById(R.id.iv_share)
        val cardView: CardView = view.findViewById(R.id.cv_card_repositorio)
    }
}