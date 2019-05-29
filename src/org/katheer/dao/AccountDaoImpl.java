package org.katheer.dao;

import org.katheer.dto.Account;
import org.katheer.mapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Map<String, Object> params;
    private String query;

    @Override
    public int createAccount(Account account) {
        try {
            query = "INSERT INTO account(name, mobile, email, branch, balance) VALUES(:name, :mobile, :email, :branch, :initialBalance)";
            params = new HashMap<>();
            params.put("name", account.getName());
            params.put("mobile", account.getMobile());
            params.put("email", account.getEmail());
            params.put("branch", account.getBranch());
            params.put("initialBalance", account.getBalance());

            jdbcTemplate.update(query, params);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        //returning account number to user
        query = "SELECT * FROM account WHERE name=:name AND mobile=:mobile " +
                "AND email=:email";
        params = new HashMap<>();
        params.put("name", account.getName());
        params.put("mobile", account.getMobile());
        params.put("email", account.getEmail());
        return jdbcTemplate.query(query, params, new AccountRowMapper()).get(0).getAccNo();
    }

    @Override
    public Account getAccount(int accNo) {
        Account account = null;
        try {
            query = "SELECT * FROM account WHERE accNo=:accNo";
            params = new HashMap<>();
            params.put("accNo", accNo);

            List<Account> accounts = jdbcTemplate.query(query, params,
                    new AccountRowMapper());
            account = accounts.size() == 0 ? null : accounts.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return account;
        }
        return account;
    }

    @Override
    public double deposit(int accNo, double amount) {
        try {
            query = "UPDATE account SET balance=:newBalance WHERE accNo=:accNo";
            params = new HashMap<>();
            params.put("newBalance", amount);
            params.put("accNo", accNo);

            int rowCount = jdbcTemplate.update(query, params);

            if (rowCount == 1) {
                return amount;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public double withdraw(int accNo, double amount) {
        try {
            query = "UPDATE account SET balance=:balance WHERE accNo=:accno";
            params = new HashMap<>();
            params.put("balance", amount);
            params.put("accno", accNo);

            int rowCount = jdbcTemplate.update(query, params);

            if (rowCount == 1) {
                return amount;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
