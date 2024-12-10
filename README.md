## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
## Preprocessing 
**Input**: IND,RAIN,IND.1,T.MAX,IND.2,T.MIN,T.MIN.G

**Output**: WIND

**Insignificant feature**: DATE
- Load data, read data : LoadData(), printTop10Data()
- Central Tendency of data: calculateMean()
- Remove Columns: removeLabel()
- Dispersion of data: calculateStandardDeviation(), calculatePercentile()
- Check duplicated, null : cleanNullValue()
- Remove outliers: removeOutliers()
- Featuring data: replaceStringByDouble()
## Execution Guide
### **Step 1**: Download Java Package
Ensure you have the Java Development Kit (JDK) installed on your computer. You can download and install the JDK from the official Oracle website.
Verify the installed JDK version by opening the terminal (or Command Prompt) and running the following command: java -version
### **Step 2**: Download the Weka Library
Download the weka.jar file from the following link: Download [Weka](http://www.java2s.com/Code/Jar/w/Downloadwekajar.htm)
After downloading, unzip the weka-3-7-0.zip file to extract the weka.jar file.
Move the weka.jar file to the directory containing your Java source code.
### **Step 3**: Run the Program
Open your Integrated Development Environment (IDE) (such as Eclipse, IntelliJ IDEA, or any other preferred IDE).

Create a new Java project and add the weka.jar file to the project's libraries:

In Eclipse: Right-click on the project -> Properties -> Java Build Path -> Libraries -> Add External JARs -> Select the weka.jar file.
In IntelliJ IDEA: Right-click on the project -> Open Module Settings -> Libraries -> Add -> Java -> Select the weka.jar file.
Ensure that the App.java file is located in the src directory of your project.

Open the **App.java** file, locate the **main method**, and run the program by right-clicking on the App.java file and selecting "Run As" -> "Java Application".

# Notes:
- Ensure that you have correctly configured the path to the weka.jar file in your project.
- If you encounter any errors while running the program, review the steps above and make sure all necessary libraries and data have been downloaded and configured correctly.
