#include <iostream>
#include <bits/stdc++.h>

#include "Crawler/Crawler.h"
#include "Post Objects/RedditPost.h"
// #include "Post Objects/RedditComment.h"

using namespace std;

int main(void)
{

    string input;

    // Start program
    cout << "ICT1009 C++ Crawler by Team __" << endl;

    // Constatntly prompts user until exit
    while (1 == 1)
    {
        // Take user input
        cout << "\nPlease select what you would like to do:" << endl;
        cout << " > Crawl\n > Load\n > Statistics\n > Export\n > Help\n > Exit" << endl;
        cout << "\nInput: ";
        cin >> input;

        // Convert to lower case
        transform(input.begin(), input.end(), input.begin(), ::tolower);

        //Executes functions
        if (input.compare("exit") == 0)
        {
            cout << "\nGoodbye!" << endl;
            break;
        }
        else if (input.compare("crawl") == 0)
        {

            // Collects defined search query
            string query;
            cout << "\nPlease enter a search query: ";
            cin >> query;

            //Crawls for data
            Crawler crawler;

            cout << "Data crawled for '" << query << "'!\nYou may now export using the 'Export' command or display statistics with the 'Statistics' command." << endl;
        }
        else if (input.compare("load") == 0)
        {
            // Prompts user for filename
            string filename;
            cout << "\nPlease enter filename: ";
            cin >> filename;

            // Loads data from existing file
            // if no file is found, print error
            // if file is found, load into objects
        }
        else if (input.compare("statistics") == 0)
        {
            // Displays statistics for crawled data
            // if no data found in objects, print error
            // if data is found, display stats
            cout << "Displaying Statistics for ___" << endl;
        }
        else if (input.compare("export") == 0)
        {
            // Prompts for filename
            string filename;
            cout << "\nPlease enter filename: ";
            cin >> filename;

            // Exports data to file
            // if no data present, print error
            // if data is present, export to file
        }
        else if (input.compare("help") == 0)
        {
            cout << "\n> Crawl" << endl;
            cout << "\tCrawls for data from Reddit and __ based on entered search query.\n"
                 << endl;
            cout << "> Load" << endl;
            cout << "\tLoads crawled data from existing file.\tMust have previously exported data.\n"
                 << endl;
            cout << "> Statistics" << endl;
            cout << "\tDisplays statistics from the crawled data.\n\tMust have either crawled or imported data.\n"
                 << endl;
            cout << "> Export" << endl;
            cout << "\tExports crawled data to a file.\n\tMust have existing crawled or imported data.\n"
                 << endl;
        }
        else
        {
            cout << "\nPlease enter a valid input!" << endl;
        }
    }

    return 0;
}