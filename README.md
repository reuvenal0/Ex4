# Ex4 - Enhanced graphic spreadsheet
**The program is a graphical spreadsheet application in Java, supporting text, numbers, formulas, conditions (`IF`), and functions (`min`, `max`, `sum`, `average`), including a graphical user interface, error handling, and file save/load functionality.**

This is a solution for assignment 4: Final Assignment, in Introduction to Computer Science, 2025A at Ariel University, School of Computer Science.

This project is based on **Assignment 2** [(link to my solution for Ex2)](https://github.com/reuvenal0/Ex2.git), It expands upon the initial version by adding support for:
 - Conditional cells (using IF conditions).
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
- The image below demonstrates the program, showing all types of cells: Text, Number, Formula, Error in the formula, and Circular Dependency Error:
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
    - Use conditions with `IF` (e.g., `=if(A1>10, High, Low)`).
    - Use functions for range calculations (e.g., `=sum(A1:C3)`).
---

## 🩹 Error Handling
- **Circular dependency detection**: Identifies and alerts circular references.
- **Invalid Formula and Condition Detection**:
    - Invalid formula syntax (`ERR_FORM_FORMAT`)
    - Circular dependencies on formula (`ERR_CYCLE_FORM`)
    - Invalid conditions (`ERR_IF`)
    - Invalid functions (`ERR_FUNC`)
---

## 🧪 Testing
The project includes comprehensive **JUnit tests** for all core functionalities, including:
- **Basic cell operations** (text, number, formula, condition, function).
- **Circular dependency detection**.
- **Formula evaluation** and **syntax error detection**.
- **Function calculations** over a range of cells.
- **condition evaluation** and **return expected results**.
- **Error handling** for invalid inputs.
- **File I/O operations** for save/load functionality.
---

## 📌 Some Cells examples
### **Basic Cell Types**

1. **Number cell:**  
   ```123```

2. **Text cell:**  
   ```"Hello, World!"```
### **Formula Cells**

3. **basic arithmetic:**  
   ```=A1 + B2```

4. **multiple operations and parentheses:**  
   ```=(A1 + B2) * (C3 / D4) - 5```

### **Conditional (IF) Cells**

5. **Basic `IF` condition:**  
   ```=IF(A1>10, "High", "Low")```

6. **Nested `IF` condition (multiple conditions):**  
   ```=IF(B2<=5, "Small", IF(B2<=10, "Medium", "Large"))```

### **Function Cells**
7. **Simple function (sum of a range):**  
   ```=SUM(A1:A5)```

### **Circular dependency**
8.**Circular dependency example - formula cycle error::**  
    ```A1 == B1 + 5```  
    ```B1 == A1 - 3```


## 🧩 My Class Structure
- `SCell`: Individual cell - implementation of the `Cell` interface
  - Stores cell data, type, and order.
  - Supported data: text, numbers, formulas, conditions (`IF`), and functions (`min`, `max`, `sum`, `average`).
  - Initial type verification and error handling (`ERR_FORM_FORMAT`, `ERR_CYCLE_FORM`, `ERR_IF`, `ERR_FUNC`).

- `Ex2Sheet`: Main spreadsheet - implementation of the `Sheet` interface
  - Creates and manages the 2D array of cells.
  - Handles formula, conditions (`IF`) and functions evaluation.
  - Identifies errors in formula calculation, circular references, and invalid conditions/functions.
  - Calculates the depth of cells: Counting dependencies for accurate evaluation order.
  - Implements save/load functionality.

- `CellEntry`: Cell coordinate handler - implementation of the `Index2d` interface
  - Converts between string coordinates (`"A1"`, `"B2"`) and numeric indices (`x`, `y`).
  - Validates cell references as valid Indexes.

- `Range2D`: Range handler for cell groups (e.g., `A1:C3`)
  - Represents a 2D range of cells using start and end coordinates.
  - Validates range format.
  - Integrates with `Ex2Sheet` for efficient range evaluations (ensures the range is within spreadsheet boundaries).
---


