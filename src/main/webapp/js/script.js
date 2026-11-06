
document.addEventListener("DOMContentLoaded", function () {

    // PROJECT CONTEXT PATH
    const firstPath = window.location.pathname.split("/").filter(Boolean)[0];
    const contextPath = firstPath ? "/" + firstPath : "";
    const servletURL = contextPath + "/StudentServlet";
    console.log("Context Path:", contextPath);
	console.log("Servlet URL:", servletURL);
	
	let currentPage = 1;
	let rowsPerPage = 10;
	let allStudentRows = [];

    // DISPLAY SUCCESS AND ERROR MESSAGES
    function showMessage(elementId, message, success) {
        const element = document.getElementById(elementId);

        element.textContent = message;
        element.style.color = success ? "green" : "red";
        element.style.fontWeight = "bold";
    }

    // REFRESH TABLE AND DASHBOARD
    function refreshData() {
        loadStudents();
        loadStudentCount();
        loadDepartmentCount();
        loadAverageMarks();
    }

    // ADD STUDENT
    const studentForm = document.getElementById("studentForm");

    if (studentForm) {
        studentForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const id = document.getElementById("id").value.trim();
            const name = document.getElementById("name").value.trim();
            const department = document.getElementById("department").value.trim();
            const marks = document.getElementById("marks").value.trim();

            if (!id || !name || !department || !marks) {
                showMessage("addMessage", "Please fill in all fields.", false);
                return;
            }

            if (!Number.isInteger(Number(id)) || Number(id) <= 0) {
                showMessage("addMessage", "Enter a valid positive Student ID.", false);
                return;
            }

            const marksNumber = Number(marks);

            if (!Number.isFinite(marksNumber) ||
                marksNumber < 0 || marksNumber > 100) {
                showMessage("addMessage", "Marks must be between 0 and 100.", false);
                return;
            }

            const data = new URLSearchParams();
            data.append("id", id);
            data.append("name", name);
            data.append("department", department);
            data.append("marks", String(marksNumber));

            fetch(servletURL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            })
            .then(function (response) {
                return response.text().then(function (message) {
                    if (!response.ok) {
                        throw new Error(message);
                    }
                    return message;
                });
            })
            .then(function (message) {
                showMessage("addMessage", "Student added successfully!", true);
                studentForm.reset();
                refreshData();
            })
            .catch(function (error) {
                showMessage("addMessage", "Add failed: " + error.message, false);
            });
        });
    }
    // SEARCH STUDENT
    const searchButton = document.querySelector(".search-btn");

    if (searchButton) {
        searchButton.addEventListener("click", function () {
            const searchId = document.getElementById("searchId").value.trim();

            if (!searchId) {
    			document.getElementById("searchResult").textContent = "Please enter a Student ID.";
    			return;
            }

            fetch(servletURL + "?id=" + encodeURIComponent(searchId))
            .then(function (response) {
                return response.text();
            })
            .then(function (result) {
                document.getElementById("searchResult").innerHTML = result;
            })
            .catch(function (error) {
                console.error("Search error:", error);
            });
        });
    }

    // EDIT STUDENT
    window.editStudent = function (id) {

        fetch(servletURL + "?type=student&id=" + encodeURIComponent(id))
        .then(function (response) {
            if (!response.ok) {
                throw new Error("Student not found.");
            }
            return response.json();
        })
        .then(function (student) {

            document.getElementById("updateId").value = student.id;
            document.getElementById("updateName").value = student.name;
            document.getElementById("updateDepartment").value = student.department;
            document.getElementById("updateMarks").value = student.marks;

            const updateForm = document.getElementById("updateForm");

            if (updateForm) {
                updateForm.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });
            }

            showMessage(
                "updateMessage",
                "Student details loaded successfully!",
                true
            );
        })
        .catch(function (error) {
            showMessage("updateMessage", "Edit failed: " + error.message, false);
            console.error(error);
        });
    };

    // UPDATE STUDENT
    const updateForm = document.getElementById("updateForm");

    if (updateForm) {
        updateForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const id = document.getElementById("updateId").value.trim();
            const name = document.getElementById("updateName").value.trim();
            const department = document.getElementById("updateDepartment").value.trim();
            const marks = document.getElementById("updateMarks").value.trim();

            if (!id || !name || !department || !marks) {
                showMessage("updateMessage", "Please fill in all fields.", false);
                return;
            }

            const marksNumber = Number(marks);

            if (!Number.isFinite(marksNumber) ||
                marksNumber < 0 || marksNumber > 100) {
                showMessage("updateMessage", "Marks must be between 0 and 100.", false);
                return;
            }

            const data = new URLSearchParams();
            data.append("action", "update");
            data.append("id", id);
            data.append("name", name);
            data.append("department", department);
            data.append("marks", String(marksNumber));

            fetch(servletURL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            })
            .then(function (response) {
                return response.text().then(function (message) {
                    if (!response.ok) {
                        throw new Error(message);
                    }
                    return message;
                });
            })
            .then(function () {
                showMessage(
                    "updateMessage",
                    "Student updated successfully!",
                    true
                );
                refreshData();
            })
            .catch(function (error) {
                showMessage(
                    "updateMessage",
                    "Update failed: " + error.message,
                    false
                );
            });
        });
    }
    
// DELETE STUDENT
window.deleteStudent = function (id) {

    const confirmDelete = window.confirm(
        "Are you sure you want to delete this student?"
    );

    if (!confirmDelete) {
        return;
    }

    const data = new URLSearchParams();
    data.append("action", "delete");
    data.append("id", id);

    fetch(servletURL, {
        method: "POST",
        body: data
    })
    .then(function (response) {
        return response.text().then(function (message) {
            if (!response.ok) {
                throw new Error(message);
            }
            return message;
        });
    })
    .then(function (message) {

        showMessage(
            "deleteMessage",
            "Student deleted successfully!",
            true
        );

        refreshData();
    })
    .catch(function (error) {

        showMessage(
            "deleteMessage",
            "Delete failed: " + error.message,
            false
        );

        console.error("Delete error:", error);
    });
};

    // VIEW STUDENTS
    function loadStudents() {
    fetch(servletURL)
        .then(function (response) {
            if (!response.ok) {
                throw new Error("Unable to load students.");
            }
            return response.text();
        })
        .then(function (result) {
            const tbody = document.getElementById("studentTableBody");
            tbody.innerHTML = result;

            allStudentRows = Array.from(tbody.querySelectorAll("tr"));

            currentPage = 1;
            displayStudentPage();
        })
        .catch(function (error) {
            console.error("Students error:", error);
        });
}
function displayStudentPage() {
    const searchValue = document
        .getElementById("tableSearch")
        .value.toLowerCase();

    const filteredRows = allStudentRows.filter(function (row) {
        const name = row.cells[1].textContent.toLowerCase();
        const department = row.cells[2].textContent.toLowerCase();

        return name.includes(searchValue) ||
               department.includes(searchValue);
    });

    const totalPages = Math.max(
        1,
        Math.ceil(filteredRows.length / rowsPerPage)
    );

    if (currentPage > totalPages) {
        currentPage = totalPages;
    }

    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;

    allStudentRows.forEach(function (row) {
        row.style.display = "none";
    });

    filteredRows.slice(start, end).forEach(function (row) {
        row.style.display = "";
    });

    document.getElementById("pageInfo").textContent =
        "Page " + currentPage + " of " + totalPages;

    document.getElementById("prevPage").disabled =
        currentPage === 1;

    document.getElementById("nextPage").disabled =
        currentPage === totalPages;
}
document.getElementById("prevPage").addEventListener("click", function () {
    if (currentPage > 1) {
        currentPage--;
        displayStudentPage();
    }
});

document.getElementById("nextPage").addEventListener("click", function () {
    currentPage++;
    displayStudentPage();
});

document.getElementById("pageSize").addEventListener("change", function () {
    rowsPerPage = Number(this.value);
    currentPage = 1;
    displayStudentPage();
});
    
    function applyTableFilter() {
    currentPage = 1;
    displayStudentPage();
}
    const tableSearch = document.getElementById("tableSearch");

	if (tableSearch) {
    	tableSearch.addEventListener("input", applyTableFilter);
	}

    // TOTAL STUDENTS
    function loadStudentCount() {
        fetch(servletURL + "?type=count")
        .then(function (response) {
            return response.text();
        })
        .then(function (result) {
            document.getElementById("totalStudents").textContent = result;
        })
        .catch(function (error) {
            console.error("Student count error:", error);
        });
    }

    // TOTAL DEPARTMENTS
    function loadDepartmentCount() {
        fetch(servletURL + "?type=departments")
        .then(function (response) {
            return response.text();
        })
        .then(function (result) {
            document.getElementById("departmentCount").textContent = result;
        })
        .catch(function (error) {
            console.error("Department count error:", error);
        });
    }

    // AVERAGE MARKS
    function loadAverageMarks() {
        fetch(servletURL + "?type=average")
        .then(function (response) {
            return response.text();
        })
        .then(function (result) {
            document.getElementById("averageMarks").textContent = result;
        })
        .catch(function (error) {
            console.error("Average marks error:", error);
        });
    }
    
    function sortStudentTable(columnIndex, dataType, direction) {

    allStudentRows.sort(function (rowA, rowB) {

        const valueA = rowA.cells[columnIndex].textContent.trim();
        const valueB = rowB.cells[columnIndex].textContent.trim();

        let comparison;

        if (dataType === "number") {
            comparison = Number(valueA) - Number(valueB);
        } else {
            comparison = valueA.localeCompare(valueB);
        }

        return direction === "asc" ? comparison : -comparison;
    });

    const tbody = document.getElementById("studentTableBody");

    allStudentRows.forEach(function (row) {
        tbody.appendChild(row);
    });

    currentPage = 1;
    displayStudentPage();
}
const sortableHeaders = document.querySelectorAll("th[data-sort]");

sortableHeaders.forEach(function (header) {

    header.dataset.direction = "asc";

    header.addEventListener("click", function () {

        const headers = Array.from(sortableHeaders);
        const columnIndex = headers.indexOf(header);

        const direction = header.dataset.direction;

        sortStudentTable(
            columnIndex,
            header.dataset.sort,
            direction
        );

        header.dataset.direction =
            direction === "asc" ? "desc" : "asc";
    });
});

    // INITIAL PAGE LOAD
    refreshData();

});
