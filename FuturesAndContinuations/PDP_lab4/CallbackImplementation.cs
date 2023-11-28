using System.Net.Sockets;
using System.Text;

namespace PDP_lab4
{
    public class CallbackImplementation
    {
        public static void Run(string[] hosts)
        {
            for (var i = 0; i < hosts.Length; i++)
            {
                StartDownload(i, hosts[i]);
                Thread.Sleep(2000); 
            }
        }

        private static void StartDownload(int id, string host)
        {
            //configure the connection information
            var state = Utils.GetState(id, host);

            //begin a request for the remote host connection
            //remoteEP (EndPoint) - an EndPoint that represents the remote host
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - an object that contains state information for the request
            state.socket.BeginConnect(state.remoteEndPoint, ConnectCallback, state);
        }

        private static void ConnectCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending connection request
            state.socket.EndConnect(asyncResult);

            //print the status
            Console.WriteLine("(" + state.id + ") connected to " + state.host);

            //convert the get request string to bytes and add to buffer
            var buffer = Encoding.ASCII.GetBytes(Utils.GetRequest(state.host, state.endPointPath));

            //send data to the connected socket
            //buffer (Byte[]) - the buffer that contains the data to send
            //offset (Int32) - the position in the buffer at which to begin sending data
            //size (Int32) - the number of bytes to send
            //socketFalgs (SocketFlags) - a bitwise combination of the SocketFlags values
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - an object that contains state information for the send operation
            state.socket.BeginSend(buffer, 0, buffer.Length, 0, SendCallback, state);
        }

        private static void SendCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending send started by BeginSend
            //asyncResult (IAsyncResult) - the state information created by the matching BeginSend call
            var numberOfBytesSent = state.socket.EndSend(asyncResult);

            //print the status
            Console.WriteLine("(" + state.id + ") sent " + numberOfBytesSent + " bytes");

            //begin to receive data from the connected socket
            //buffer (Byte[]) - the storage location for the received data
            //offset (Int32) - the location in buffer to store the received data
            //size (Int32) - the number of bytes to receive
            //socketFlags (SocketFlags) - a bitwise combination of the SocketFlags values
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - a user-defined object that contains information about the receive operation
            state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);
        }

        private static void ReceiveCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending read started by BeginReceive
            //asyncResult (IAsyncResult) - the state information created by the matching BeginRecieve call
            var numberOfBytesReceived = state.socket.EndReceive(asyncResult);

            //convert the bytes from the buffer to string and append to response
            state.response.Append(Encoding.ASCII.GetString(state.buffer, 0, numberOfBytesReceived));

            //check the response header
            if (!Utils.CheckResponseHeader(state.response.ToString()))
            {
                //the response header is not complete

                //get the next part of the header
                state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);
            }
            else
            {
                //the response header is complete

                //get the content length from the header line
                var contentLength = Utils.GetContentLength(state.response.ToString());

                //get the body of the response
                var responseBody = Utils.GetResponseBody(state.response.ToString());

                //compare the length of the response body to the content length from the header line
                if (responseBody.Length < contentLength)
                {
                    //the response body is not complete

                    //get the next part of the response body
                    state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);
                }
                else
                {
                    //the response body is complete

                    //print the response
                    Console.WriteLine(state.response.ToString());

                    //close the connection
                    state.socket.Shutdown(SocketShutdown.Both);
                    state.socket.Close();
                }
            }
        }
    }
}
