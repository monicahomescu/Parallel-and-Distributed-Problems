using System.Net.Sockets;
using System.Net;

namespace PDP_lab4
{
    public class Utils
    {
        public static State GetState(int id, string host)
        {
            //resolve the host name to get the IP address
            var hostEntry = Dns.GetHostEntry(host.Split('/')[0]);

            //get the first IP address from the resolved host entry
            var ipAddress = hostEntry.AddressList[0];

            //create a new State object and initialize its properties
            var state = new State
            {
                id = id,  //set the id property
                host = host.Split('/')[0],  //extract the host from the input (up to the first '/')
                socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp),  //create a new socket
                endPointPath = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/",  //extract the endpoint path (after the first '/')
                remoteEndPoint = new IPEndPoint(ipAddress, 80)  //create a new IPEndPoint with the resolved IP address and port 80
            };

            //return the created State object
            return state;
        }

        public static string GetRequest(string host, string endPointPath)
        {
            return "GET " + endPointPath + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" +
               "Content-Length: 0\r\n\r\n"; //there is no request body, so the content length is 0
        }

        public static bool CheckResponseHeader(string response)
        {
            //check for the delimiter indicating the end of the response header and the start of the response body
            return response.Contains("\r\n\r\n");
        }

        public static int GetContentLength(string response)
        {
            //split the response into lines
            var lines = response.Split('\r', '\n');

            //iterate through each line in the response
            foreach (var line in lines)
            {
                //split each line in two header parts, title and value
                var headerParts = line.Split(':');

                //check if the header is Content-Length
                if (headerParts[0].CompareTo("Content-Length") == 0)
                {
                    //parse and return the Content-Length value
                    return int.Parse(headerParts[1]);
                }
            }

            return 0;
        }

        public static string GetResponseBody(string response)
        {
            //split the response by the delimiter that is between the response header and the response body
            var responseBody = response.Split(new[] { "\r\n\r\n" }, StringSplitOptions.RemoveEmptyEntries);

            //check if there is a response body
            if (responseBody.Length > 1)
            {
                //return the response body
                return responseBody[1];
            }
            else
            {
                //return an empty string
                return "";
            }
        }
    }
}
