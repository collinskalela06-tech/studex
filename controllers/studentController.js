const db = require("../config/db");

exports.getStudents = (req, res) => {

    db.query(
        "SELECT * FROM students",
        (err, results) => {

            if (err) {
                return res.status(500).json({
                    message: err.message
                });
            }

            res.json(results);
        }
    );
};

exports.addStudent = (req, res) => {

    const {
        student_name,
        student_number,
        program_id,
        group_id
    } = req.body;

    const sql = `
        INSERT INTO students
        (
            student_name,
            student_number,
            program_id,
            group_id
        )
        VALUES
        (?,?,?,?)
    `;

    db.query(
        sql,
        [
            student_name,
            student_number,
            program_id,
            group_id
        ],
        (err, result) => {

            if (err) {
                return res.status(500).json({
                    message: err.message
                });
            }

            res.json({
                message: "Student Added"
            });

        }
    );
};