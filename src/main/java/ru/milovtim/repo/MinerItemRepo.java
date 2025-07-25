package ru.milovtim.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Component;
import ru.milovtim.domain.MinerItem;

import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

@Component
public class MinerItemRepo {
    private static final RowMapper<MinerItem> ROW_MAPPER = (rs, rowNum) ->
            new MinerItem()
//                    .id(((UUID) rs.getObject("id")))
                    .alias(rs.getString("alias"))
                    .ipAddr(rs.getString("ip_addr"))
                    .login(rs.getString("miner_login"))
                    .password(rs.getString("miner_password"));
            ;
    public final JdbcTemplate jdbcTemplate;

    public MinerItemRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<MinerItem> findByAlias(String alias) {
        MinerItem minerItem = jdbcTemplate.queryForObject("SELECT * FROM \"miner_items\" mi WHERE mi.\"alias\" = ?",
                ROW_MAPPER, new SqlParameterValue(Types.VARCHAR, alias));
        return Optional.ofNullable(minerItem);
    }

}
