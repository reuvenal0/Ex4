# Ex4 - Enhanced Graphic Spreadsheet
***מה זה?

This is a solution for assignment 4: Final Assignment, in Introduction to Computer Science, 2025A at Ariel University, School of Computer Science.

This project is an advanced solution for **Assignment 2 (Ex2)**, It expands upon the initial version by adding support for:
 - Conditional cells (using IF conditions)
 - Functions (min, max, sum, and average) for numerical calculations over a range of cells.
---


## 🔑 Key Features
- **Graphical User Interface (GUI)**: A user-friendly interface for viewing and editing the spreadsheet.
- **Text, Numbers, and Formulas**: Basic cell data types including arithmetic formulas (`+`, `-`, `*`, `/`).
- **Cell References**: Support for cell references like `A1`, `B2`, enabling dynamic calculations.
- **Conditional Cells**: `IF` conditions with support for:
    - Comparison operators: `<`, `>`, `==`, `<=`, `>=`, `!=`
    - Nested conditions and logical operations
- **Functions**: Calculation over a range of cells with:
    - `min`: Minimum value in a range (e.g., `=min(A1:C3)`)
    - `max`: Maximum value in a range (e.g., `=max(A1:C3)`)
    - `sum`: Sum of all values in a range (e.g., `=sum(A1:C3)`)
    - `average`: Average of all values in a range (e.g., `=average(A1:C3)`)
- **File I/O**: Save and load spreadsheet data, including all advanced functionalities.

---

## 📸 Demo Screenshot
??
---


## 🔧 Installation & Setup
1. **Clone the repository:**
   - Using the following commands: 
   ```sh
    git clone https://github.com/reuvenal0/Ex4.git
    ```
2. **Open in IntelliJ IDEA or other Java IDE**:
    - Ensure you have JDK 11 or later installed.
3. **Build & Run**:
   - Run the GUI application by executing the `Ex2GUI` class.
---

## 🚀 Usage
- **Navigating the GUI**:
    - Click on a cell to select it.
    - Type to edit the selected cell.
    - Press `Enter` to save the cell content.
    - Use `Save` and `Load` buttons to persist data.
- **Adding Data**:
    - Enter text directly (e.g., `Hello`).
    - Enter numbers (e.g., `123`).
    - Use formulas with `=` prefix (e.g., `=A1+B2`).
    - Use conditions with `IF` (e.g., `=if(A1>10, "High", "Low")`).
    - Use functions for range calculations (e.g., `=sum(A1:C3)`).
---

## 🧩My Class Structure


## 🩹 Error Handling
- **Circular Dependency Detection**: Identifies and alerts circular references.
- **Invalid Formula and Condition Detection**:
    - Invalid formula syntax (`ERR_FORM_FORMAT`)
    - Circular dependencies on formula (`ERR_CYCLE_FORM`)
    - Invalid conditions (`ERR_IF`)
    - Invalid functions (`ERR_FUNC`)
---

## 🧪 Testing
The project includes comprehensive **JUnit tests** for all core functionalities, including:
- **Basic cell operations** (text, number, formula, condition, function).
- **Formula evaluation** and **circular dependency detection**.
- **Function calculations** over a range of cells.
- **Error handling** for invalid inputs.
- **File I/O operations** for save/load functionality.
---
