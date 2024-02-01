package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var nomeUsuario: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var listaRepositories: RecyclerView
    private lateinit var githubApi: GitHubService
    private lateinit var repositoryAdapter: RepositoryAdapter
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        supportActionBar!!.hide()
        setupView()
        showUserName()
        setupRetrofit()
        setupAdapter(emptyList())
        setupListeners()
    }

    // Método responsável por realizar o setup da view e recuperar os Ids do layout
    private fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
        progressBar = findViewById(R.id.pg_bar)
    }

    // Método responsável por configurar os listeners click da tela
    private fun setupListeners() {
        val buttonConfirm = findViewById<Button>(R.id.btn_confirmar)
        buttonConfirm.setOnClickListener {
            val username = nomeUsuario.text.toString()
            lifecycle.apply {
                    saveUserLocal(username)
                    getAllReposByUserName(username)
                }

            }

        }


    // Salvar o usuário preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(username: String) {
        val sharedPreferences = getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    // Exibir sempre as informações no EditText se a SharedPreferences possuir algum valor
    private fun showUserName() {
        val sharedPreferences = getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        nomeUsuario.setText(username)
    }

    // Método responsável por fazer a configuração base do Retrofit
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    // Método responsável por buscar todos os repositórios do usuário fornecido
    private fun getAllReposByUserName(username: String) {

        progressBar.visibility = View.VISIBLE

        val call = githubApi.getAllRepositoriesByUser(username)
        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                try {

                    if (response.isSuccessful) {
                        progressBar.visibility =  View.GONE
                        val repos = response.body() ?: emptyList()
                        updateUIWithRepositories(repos)
                    } else {

                        updateUIWithRepositories(emptyList())
                    }
                }catch (ex: Exception){

                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                // Tratar o erro
                // TODO: Tratar o erro
                updateUIWithRepositories(emptyList())
            }
        })
    }

    // Método responsável por realizar a configuração do adapter
    private fun setupAdapter(list: List<Repository>) {
        repositoryAdapter = RepositoryAdapter(list)
        repositoryAdapter.btnShareListener = { repository ->
            shareRepositoryLink(repository.htmlUrl)
        }
        listaRepositories.layoutManager = LinearLayoutManager(this)
        listaRepositories.adapter = repositoryAdapter
    }

    // Método responsável por compartilhar o link do repositório selecionado
    private fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Método responsável por abrir o navegador com o link informado do repositório
    private fun openBrowser(urlRepository: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlRepository)))
    }

    private fun updateUIWithRepositories(repositories: List<Repository>) {
        repositoryAdapter.updateData(repositories)
    }


}
