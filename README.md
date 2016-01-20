
ReadMe

How to run:
java -jar assignment.jar weburl 
(e.g. java -jar assignment.jar "http://blog.rei.com/camp/how-to-introduce-your-indoorsy-friend-to-the-outdoors/")

Strategy:
1. Get web url from terminal
2. Use Jsoup to get web page content, both html content and javascript content
3. Load stopwords from external txt files into set
4. Parse html content and javascript content into words, stored in two hashmap
5. Count the frequency for each word, check if it is stopword, if so, skip it. Otherwise, increase frequency by 1
6. Display the words from highest frequency to lowest where frequency larger and equal to a pre-set value. (10 as default)

PS:
1. High frequency words in html content and javascript content are displayed seperately, since I don't have a whole list of stopwords for javascript. So some javascript tags and words with no meaning can have a high frenquency but don't describe anything to the page.
2. I only count single word frequency and I don't have a good way to combine words into a longer one for better topic description (like combine Toaster 2slice into 2slice Toaster). Need more knowledge on how to do so.

Contact info:
Name: Xiaoran Hu
Email: huxiaora@usc.edu
phone: 614-886-2780

Thank you very much!
