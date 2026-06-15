// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context';
import { Navbar } from './components/layout';
import { Login, Profile, Signup } from './pages';
import { ProblemList } from './pages/ProblemList/ProblemList';
import { Workspace } from './pages/Workspace/Workspace';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-dark-bg flex flex-col">
          <Navbar />
          <main className="flex-1 overflow-auto">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/problems" element={<ProblemList />} />
              <Route path="/problems/:problemId" element={<Workspace />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/profile/:userId" element={<Profile />} />
              {/* Add your Problem List and Workspace routes here next */}
              <Route path="/" element={<div className="p-8 text-white">Dashboard Comming Soon</div>} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;