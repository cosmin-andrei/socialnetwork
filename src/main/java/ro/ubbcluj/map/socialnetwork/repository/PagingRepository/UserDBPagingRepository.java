package ro.ubbcluj.map.socialnetwork.repository.PagingRepository;

import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.domain.validators.UtilizatorValidator;
import ro.ubbcluj.map.socialnetwork.repository.UserDBRepository;
import ro.ubbcluj.map.socialnetwork.repository.paging.Page;
import ro.ubbcluj.map.socialnetwork.repository.paging.Pageable;
import ro.ubbcluj.map.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, Utilizator> {


    private Connection connection;

    public UserDBPagingRepository(UtilizatorValidator validator, String url, String username, String password) {
        super(validator, url, username, password);
        try {
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Page<Utilizator> findAllOnPage(Pageable pageable) {

        List<Utilizator> utilizators = new ArrayList<>();
        try (
                PreparedStatement pagestatement = connection.prepareStatement("SELECT * from users limit ? offset ?");
                PreparedStatement countstatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM users")
        ) {

            pagestatement.setInt(1,pageable.getPageSize());
            pagestatement.setInt(2,pageable.getPageSize() *pageable.getPageNr());

            try(
                    ResultSet pageResultSet = pagestatement.executeQuery();
                    ResultSet countResultSet = countstatement.executeQuery()
            ){
                int count = 0;
                if(countResultSet.next()){
                    count = countResultSet.getInt("count");
                }

            while(pageResultSet.next()){
                Long id = pageResultSet.getLong("id");
                String username = pageResultSet.getString("username");
                String firstName = pageResultSet.getString("first_name");
                String lastName = pageResultSet.getString("last_name");
                Utilizator utilizator = new Utilizator(username, firstName, lastName);
                utilizator.setId(id);
            }

            return new Page<>(utilizators, count);

            }

//            statement.setInt(1,pageable.getPageSize());
//            statement.setInt(2,pageable.getPageSize() * (pageable.getPageNumber()-1));
//            ResultSet resultSet = statement.executeQuery();
//            Set<Utilizator> users = new HashSet<>();
//            while (resultSet.next()) {
//                Long id= resultSet.getLong("id");
//                String username = resultSet.getString("username");
//                String firstName = resultSet.getString("first_name");
//                String lastName = resultSet.getString("last_name");
//
//                Utilizator user=new Utilizator(username,firstName,lastName);
//                user.setId(id);
//                users.add(user);
//            }
//
//            return new Page<>(pageable, users.stream()) ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
