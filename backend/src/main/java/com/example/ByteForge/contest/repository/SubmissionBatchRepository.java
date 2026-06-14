package com.example.ByteForge.contest.repository;

import com.example.ByteForge.contest.dto.message.ContestSubmissionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubmissionBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertSubmissions(List<ContestSubmissionMessage> submissions) {
        String sql = "INSERT INTO submissions (problem_id, register_id, language_id, submission_code, " +
                "submission_status, code_output, user_logs, submission_date_time, submission_visibility) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ContestSubmissionMessage msg = submissions.get(i);
                ps.setLong(1, msg.getProblemId());
                ps.setLong(2, msg.getUserId());
                ps.setInt(3, msg.getLanguageId());
                ps.setString(4, msg.getSubmissionCode());
                ps.setString(5, msg.getStatus());
                ps.setString(6, msg.getCodeOutput() != null ? msg.getCodeOutput() : "");
                ps.setString(7, msg.getUserLogs() != null ? msg.getUserLogs() : "");
                ps.setTimestamp(8, Timestamp.valueOf(msg.getSubmissionTime()));
                ps.setString(9, "CONTEST");
            }

            @Override
            public int getBatchSize() {
                return submissions.size();
            }
        });
    }
}