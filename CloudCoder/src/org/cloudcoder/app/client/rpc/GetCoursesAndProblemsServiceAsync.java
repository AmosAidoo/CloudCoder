// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2012, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.cloudcoder.app.client.rpc;

import org.cloudcoder.app.shared.model.Course;
import org.cloudcoder.app.shared.model.CourseAndCourseRegistration;
import org.cloudcoder.app.shared.model.Problem;
import org.cloudcoder.app.shared.model.ProblemAndSubmissionReceipt;
import org.cloudcoder.app.shared.model.ProblemAndTestCaseList;
import org.cloudcoder.app.shared.model.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GetCoursesAndProblemsServiceAsync {

	void getCourses(AsyncCallback<Course[]> callback);

	void getCourseAndCourseRegistrations(
			AsyncCallback<CourseAndCourseRegistration[]> callback);

	void getProblems(Course course, AsyncCallback<Problem[]> callback);

	void getProblemAndSubscriptionReceipts(Course course,
			AsyncCallback<ProblemAndSubmissionReceipt[]> callback);

	void getTestCasesForProblem(int problemId,
			AsyncCallback<TestCase[]> callback);

	void storeProblemAndTestCaseList(
			ProblemAndTestCaseList problemAndTestCaseList, Course course,
			AsyncCallback<ProblemAndTestCaseList> callback);

}
