#include <iostream>
#include <bits/stdc++.h>

#include "Crawler.h"

using namespace std;

int main(void)
{
    Crawler crawler;
    string input;

    // Start program
    cout << "ICT1009 C++ Crawler by Team __" << endl;

    // Constatntly prompts user
    while (1 == 1)
    {
        // Take user input
        cout << "Please select what you would like to do:" << endl;
        cout << "> Crawl\n> Load (Must have existing saved crawled data!)\n> Statistics\n> Export\n> Exit" << endl;
        cout << "Input: ";
        cin >> input;

        // Convert to lower case
        transform(input.begin(), input.end(), input.begin(), ::tolower);

        //Executes functions
        if (input.compare("exit") == 0)
        {
            cout << "Goodbye!" << endl;
            break;
        }
        else if (input.compare("crawl") == 0)
        {
            // Crawls for data
            string query;
            cout << "Please enter a search query: ";
            cin >> query;


        }
        else if (input.compare("load") == 0)
        {
            // Loads data from existing file
            string filename;
            cout << "Please enter filename: ";
            cin >> filename;
        }
        else if (input.compare("statistics") == 0)
        {
            // Displays statistics for crawled data
            cout << "Displaying Statistics for ___" << endl;
        }
        else if (input.compare("export") == 0)
        {
            // Exports data to file
            string filename;
            cout << "Please enter filename: ";
            cin >> filename;
        }
        else
        {
            cout << "Please enter a valid input!" << endl;
        }
    }

    return 0;
}