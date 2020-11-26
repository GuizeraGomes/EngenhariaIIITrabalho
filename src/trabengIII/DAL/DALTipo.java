package trabengIII.DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import trabengIII.Banco.Conexao;
import trabengIII.Entity.Cliente;
import trabengIII.Entity.TipoProduto;

public class DALTipo {
    
    public boolean inserir(TipoProduto tip){
        
        boolean resultado = false;
        
        String sql = "insert into tipo values(default, '#2')";
        sql = sql.replaceAll("#2", tip.getTip_descricao());
        
        Conexao con = Conexao.getConexao();
        resultado = con.manipular(sql);
        
        resultado = gravarFavorito((List<Cliente>)(Cliente)(tip.getLo()),'I', tip.getTip_id());
        con.desconectar();
        
        return resultado;
    }
    
    public boolean inserirFavorito(int idTipo, int idCliente){
        
        boolean resultado = false;
        
        String sql = "insert into favorito values(#1, #2)";
        sql = sql.replaceAll("#1", ""+idTipo);
        sql = sql.replaceAll("#2", ""+idCliente);
        
        Conexao con = Conexao.getConexao();
        resultado = con.manipular(sql);
        
        return resultado;
    }
    
    public boolean gravarFavorito(List<Cliente> todos, char op, int id){
        
         boolean ok = true;

        try {
            
            Conexao con = Conexao.getConexao();
            
            con.getConnect().setAutoCommit(false);
           

            if(op == 'A')
                ok = con.manipular("delete * from favorito where = "+id);
            
            if (ok) {

                for (int i = 0; i < todos.size() && ok; i++) {

                    ok = inserirFavorito(id, todos.get(i).getCli_id());
                }
            }

            if (ok) {
                con.getConnect().commit();
            } else {
                con.getConnect().rollback();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ok = false;
        }

        return ok;
    }
    
    public boolean atualizar(TipoProduto tip){
        
        boolean resultado = false;
        String sql = "update tipo set tip_descricao = '#1' where tip_id = "+tip.getTip_id();
         
        sql = sql.replaceAll("#1", tip.getTip_descricao());
        
        Conexao con = Conexao.getConexao();
        resultado = con.manipular(sql);
        resultado = gravarFavorito((List<Cliente>)(Cliente)(tip.getLo()),'A', tip.getTip_id());
        
        con.desconectar();
        
        return resultado;
    }
    
    public boolean apagar(int id){
        
        boolean res = false;
        Conexao con = Conexao.getConexao();
        con.manipular("delete from tipo where tip_id="+id);
        
        res = con.manipular("delete * from favorito where tip_id"+id);
        con.desconectar();
        return res;
        
    }
    
    public TipoProduto get(int id){
     
        TipoProduto aux = null;
        Conexao con = Conexao.getConexao();
        ResultSet rs = con.consultar("select * from tipo where tip_id="+id);
        con.desconectar();
        
        try 
        {
            if(rs.next())
            {
                   aux = new TipoProduto(rs.getInt("tip_id"), rs.getString("tip_descricao"));
            }
        } 
        catch (SQLException ex) 
        {
            
        }
        
        return aux;
    }
    
    public List<TipoProduto> get(String filtro){
        
        String sql="select * from tipo";
        
        if(!filtro.isEmpty())
            sql+=" where "+filtro;
        
        List <TipoProduto> tipos = new ArrayList();
        Conexao con = Conexao.getConexao();
        ResultSet rs = con.consultar(sql);
        con.desconectar();
        
        try 
        {
            while(rs.next())
            {
                tipos.add(new TipoProduto(rs.getInt("tip_id"), rs.getString("tip_descricao")));
            }
        } 
        catch (SQLException ex) 
        {
            
        }
        
        return tipos;
    }
}
