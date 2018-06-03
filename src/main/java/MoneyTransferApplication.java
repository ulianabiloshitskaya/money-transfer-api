import entity.Account;
import entity.TransferEntity;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import repository.AccountDAO;
import repository.TransferDAO;
import resources.AccountResource;
import resources.AccountsResource;
import resources.MoneyTransferResource;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {
    public static void main(String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    private final HibernateBundle<MoneyTransferConfiguration> hibernate = new HibernateBundle<MoneyTransferConfiguration>(Account.class, TransferEntity.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MoneyTransferConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }

    };

    @Override
    public void initialize(Bootstrap<MoneyTransferConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<MoneyTransferConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(MoneyTransferConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(MoneyTransferConfiguration config, Environment environment) {
        final AccountDAO accountDAO = new AccountDAO(hibernate.getSessionFactory());
        final TransferDAO transferDAO = new TransferDAO(hibernate.getSessionFactory());
        environment.jersey().register(new MoneyTransferResource(accountDAO,transferDAO));
        environment.jersey().register(new AccountResource(accountDAO));
        environment.jersey().register(new AccountsResource(accountDAO));

    }

}