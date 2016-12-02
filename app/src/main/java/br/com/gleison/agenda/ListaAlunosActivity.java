package br.com.gleison.agenda;

/**
 * Created by Gleison on 30/09/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.gleison.agenda.dao.AlunoDAO;
import br.com.gleison.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

//capturando a referência o objeto ListView no design da tela da lista de Alunos
        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
//implementando o evento de click em um dos itens da lista de alunos da agenda
        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
//capturando o elemento aluno que foi clicado
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);

//iniciando uma intent que é a classe no android responsável pela chamada de uma outra tela no aplicação, ou até mesmo de aplicações externas. No android você sempre precisa enviar uma Intenção (Intent) para acessar alguma funcionalidade. E será sempre o sistema operacional que irá realizar essa chamada para as aplicações.
                Intent intentVaiPtoFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                //enviando para o formulário o aluno carregado para o objeto, quando ele foi clicado pelo usuário na lista da agenda.
                intentVaiPtoFormulario.putExtra("aluno", aluno);
//iniciando a chamada da Activity
                startActivity(intentVaiPtoFormulario);
            }
        });
//capturando o referência em tela para o botão de novo aluno
        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
//implementando o evento de click do botão
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //realizando a criação da Intent para a chamada a tela do formulário
                Intent vaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
//iniciando a chamada. Repare que como foi realizado no método anterior, utilizando o putExtra(), nesse método isso não foi necessário porque é um cadastro novo, e não será necessário recuperar os dados do aluno
                startActivity(vaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
    }

    @Override
    protected void onResume() {
//método executado quando a aplicação estava em standby e o usuário retorna para utilizá-la
        super.onResume();
        carregaLista();
    }

    private void carregaLista() {
//carregando a lista de alunos do banco de dados.
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

//criando um Array Adapter de alunso conforme realizado o carregamento do Banco de Dados SQLite e adicionando essas informações no LintView
        ArrayAdapter<Aluno> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunos);
        listaAlunos.setAdapter(adapter);
    }

    //implementando o método para deletar itens da listagem
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
//método no DAO responsável por eliminar objetos
                dao.deleta(aluno);
//fechando a conexão aberta com o banco de dados
                dao.close();

                carregaLista();
                return false;
            }
        });
    }
}

